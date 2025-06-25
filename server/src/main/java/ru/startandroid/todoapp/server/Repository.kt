package ru.startandroid.todoapp.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.LocalDate
import ru.startandroid.todoapp.server.models.ServerException
import ru.startandroid.todoapp.server.models.TodoItem
import java.security.MessageDigest
import java.sql.DriverManager
import java.sql.Statement
import java.util.UUID

class Repository {

    private val connection =
        DriverManager.getConnection("jdbc:sqlite:server/build/database.db")

    init {
        val statement: Statement = connection.createStatement()
        statement.executeUpdate(
            """
                CREATE TABLE IF NOT EXISTS "TodoItem" (
                    "id" TEXT NOT NULL,
                    "description" TEXT NOT NULL,
                    "priority" INTEGER NOT NULL,
                    "isCompleted" INTEGER NOT NULL,
                    "createdDate" TEXT NOT NULL,
                    "dueDate" TEXT,
                    "changedDate" TEXT,
                    "userId" INTEGER,
                    PRIMARY KEY("id")
                ); 
                CREATE TABLE IF NOT EXISTS "User" (
                    "userId" INTEGER NOT NULL,
                    "login"	TEXT NOT NULL,
                    "salt" TEXT NOT NULL,
                    "passwordHash" TEXT NOT NULL,
                    "accessToken" TEXT NOT NULL,
                    PRIMARY KEY("userId" AUTOINCREMENT)
                );
            """.trimIndent()
        )
    }

    suspend fun getAllItems(userId: Long): List<TodoItem> = withContext(Dispatchers.IO) {
        connection.prepareStatement(
            """
                SELECT 
                    id, 
                    description, 
                    priority, 
                    isCompleted, 
                    createdDate, 
                    dueDate, 
                    changedDate 
                FROM TodoItem 
                WHERE TodoItem.userId = ?
            """.trimIndent()
        ).use { statement ->
            statement.setLong(1, userId)
            val result = statement.executeQuery()
            buildList {
                while (result.next()) {
                    add(
                        TodoItem(
                            result.getString("id"),
                            result.getString("description"),
                            TodoItem.Priority.entries[result.getInt("priority")],
                            result.getInt("isCompleted") != 0,
                            LocalDate.parse(result.getString("createdDate")),
                            result.getString("dueDate")?.let { LocalDate.parse(it) },
                            result.getString("changedDate")?.let { LocalDate.parse(it) }
                        )
                    )
                }
            }
        }
    }

    suspend fun setAllItems(list: List<TodoItem>, userId: Long) = withContext(Dispatchers.IO) {
        connection.prepareStatement(
            """
                DELETE FROM TodoItem WHERE TodoItem.userId = ?;
            """.trimIndent()
        ).use {
            it.setLong(1, userId)
            it.executeUpdate()
        }
        for (item in list) {
            connection.prepareStatement(
                """
                    INSERT INTO TodoItem(
                        id, description, priority, isCompleted, createdDate, dueDate, changedDate, userId
                    ) VALUES(
                        ?, ?, ?, ?, ?, ?, ?, ?
                    )
                """.trimIndent()
            ).use { statement ->
                statement.setString(1, item.id)
                statement.setString(2, item.description)
                statement.setInt(3, item.priority.ordinal)
                statement.setInt(4, if (item.isCompleted) 1 else 0)
                statement.setString(5, item.createdDate.toString())
                statement.setString(6, item.dueDate?.toString())
                statement.setString(7, item.changedDate?.toString())
                statement.setLong(8, userId)
                statement.executeUpdate()
            }
        }
    }

    suspend fun addItem(todoItem: TodoItem, userId: Long) = withContext(Dispatchers.IO) {
        connection.prepareStatement(
            """
                DELETE FROM TodoItem WHERE TodoItem.id = ?;
            """.trimIndent()
        ).use { statement ->
            statement.setString(1, todoItem.id)
            statement.executeUpdate()
        }
        connection.prepareStatement(
            """
                INSERT INTO TodoItem(
                    id, description, priority, isCompleted, createdDate, dueDate, changedDate, userId
                ) VALUES(
                    ?, ?, ?, ?, ?, ?, ?, ?
                )
        """.trimIndent()
        ).use { statement ->
            statement.setString(1, todoItem.id)
            statement.setString(2, todoItem.description)
            statement.setInt(3, todoItem.priority.ordinal)
            statement.setInt(4, if (todoItem.isCompleted) 1 else 0)
            statement.setString(5, todoItem.createdDate.toString())
            statement.setString(6, todoItem.dueDate?.toString())
            statement.setString(7, todoItem.changedDate?.toString())
            statement.setLong(8, userId)
            statement.executeUpdate()

        }
    }

    suspend fun deleteItem(todoItemId: String) = withContext(Dispatchers.IO) {
        connection.prepareStatement(
            """
              DELETE FROM TodoItem WHERE TodoItem.id = ?
        """.trimIndent()
        ).use {
            it.setString(1, todoItemId)
            it.executeUpdate()
        }
    }

    suspend fun getItem(todoItemId: String): TodoItem = withContext(Dispatchers.IO) {
        connection.prepareStatement(
            """
              SELECT 
                  id, 
                  description, 
                  priority, 
                  isCompleted, 
                  createdDate, 
                  dueDate, 
                  changedDate 
              FROM TodoItem 
              WHERE TodoItem.id = ?
        """.trimIndent()
        ).use { statement ->
            statement.setString(1, todoItemId)
            val result = statement.executeQuery()
            return@use TodoItem(
                result.getString("id"),
                result.getString("description"),
                TodoItem.Priority.entries[result.getInt("priority")],
                result.getInt("isCompleted") != 0,
                LocalDate.parse(result.getString("createdDate")),
                result.getString("dueDate")?.let { LocalDate.parse(it) },
                result.getString("changedDate")?.let { LocalDate.parse(it) }
            )
        }
    }

    suspend fun register(login: String, password: String): String = withContext(Dispatchers.IO) {
        val salt = getRandomString(8)
        val passwordHash = sha256(password + salt)
        val accessToken = UUID.randomUUID().toString()

        when {
            login.length < 4 -> throw ServerException(
                errorKey = "LOGIN_TOO_SHORT",
                errorDescription = "Логин должен быть не короче 4 символов"
            )

            login.length > 30 -> throw ServerException(
                errorKey = "LOGIN_TOO_LONG",
                errorDescription = "Логин должен быть не длиннее 30 символов"
            )

            password.length < 8 -> throw ServerException(
                errorKey = "PASSWORD_TOO_SHORT",
                errorDescription = "Пароль должен быть не короче 8 символов"
            )

            password.length > 40 -> throw ServerException(
                errorKey = "PASSWORD_TOO_LONG",
                errorDescription = "Пароль должен быть не длиннее 40 символов"
            )
        }

        connection.prepareStatement(
            """
                SELECT COUNT(userId) FROM User WHERE User.login = ?
            """.trimIndent()
        ).use { statement ->
            statement.setString(1, login)
            val result = statement.executeQuery()
            result.next()
            if (result.getInt(1) > 0) {
                throw ServerException(
                    errorKey = "USER_EXISTS",
                    errorDescription = "Пользователь с таким логином уже существует"
                )
            } else {
                connection.prepareStatement(
                    """
                        INSERT INTO User (login, salt, passwordHash, accessToken)
                        VALUES (?, ?, ?, ?)
                    """.trimIndent()
                ).use { statementInsert ->
                    statementInsert.setString(1, login)
                    statementInsert.setString(2, salt)
                    statementInsert.setString(3, passwordHash)
                    statementInsert.setString(4, accessToken)
                    statementInsert.executeUpdate()
                }
                accessToken
            }
        }
    }

    suspend fun authorization(login: String, password: String): String =
        withContext(Dispatchers.IO) {
            val newToken = UUID.randomUUID().toString()
            connection.prepareStatement(
                """
                SELECT salt, passwordHash FROM User WHERE User.login = ?
            """.trimIndent()
            ).use { statement ->
                statement.setString(1, login)
                val result = statement.executeQuery()
                if (result.next().not()) {
                    throw ServerException(
                        errorKey = "LOGIN_NOT_FOUND",
                        errorDescription = "Пользователь с таким логином не найден"
                    )
                }
                val dbSalt = result.getString("salt")
                val dbPasswordHash = result.getString("passwordHash")
                val saltedIncomePassword = sha256(password + dbSalt)
                if (saltedIncomePassword == dbPasswordHash) {
                    connection.prepareStatement(
                        """
                            UPDATE User
                            SET accessToken = ?
                            WHERE User.login = ?
                        """.trimIndent()
                    ).use { statementUpdate ->
                        statementUpdate.setString(1, newToken)
                        statementUpdate.setString(2, login)
                        statementUpdate.executeUpdate()
                        newToken
                    }
                } else {
                    throw ServerException(
                        errorKey = "WRONG_PASSWORD",
                        errorDescription = "Неверный пароль"
                    )
                }
            }
        }

    suspend fun findUser(token: String): Long? = withContext(Dispatchers.IO) {
        connection.prepareStatement(
            """
                SELECT userId FROM User
                WHERE User.accessToken = ?
                """.trimIndent()
        ).use { statement ->
            statement.setString(1, token)
            val result = statement.executeQuery()
            if (result.next()) result.getLong("userId")
            else null
        }
    }

    private fun getRandomString(length: Int): String {
        var randomStr = UUID.randomUUID().toString()
        while (randomStr.length < length) {
            randomStr += UUID.randomUUID().toString()
        }
        return randomStr.substring(0, length)
    }

    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray())
        return bytesToHex(hashBytes)
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val hexString = StringBuilder()
        for (byte in bytes) {
            val hex = byte.toInt() and 0xff
            if (hex < 16) hexString.append('0')
            hexString.append(hex.toString(16))
        }
        return hexString.toString()
    }


}