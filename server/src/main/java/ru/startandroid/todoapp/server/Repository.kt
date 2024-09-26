package ru.startandroid.todoapp.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.LocalDate
import ru.startandroid.todoapp.server.models.TodoItem
import java.sql.DriverManager
import java.sql.Statement

class Repository {

    private val dbConnection =
        DriverManager.getConnection("jdbc:sqlite:server/build/database.db")

    init {
        val statement: Statement = dbConnection.createStatement()
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS TodoItem (id TEXT NOT NULL, description TEXT NOT NULL, priority INTEGER NOT NULL, isCompleted INTEGER NOT NULL, createdDate TEXT NOT NULL, dueDate TEXT, changedDate TEXT, PRIMARY KEY(id))")
    }

    suspend fun getAllItems(): List<TodoItem> = withContext(Dispatchers.IO) {
        dbConnection.use { connection ->
            connection.prepareStatement("SELECT * FROM TodoItem").use { statement ->
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
    }

    suspend fun setAllItems(list: List<TodoItem>) = withContext(Dispatchers.IO) {
        dbConnection.use { connection ->
            connection.prepareStatement(
                """
                DELETE FROM TodoItem;
            """.trimIndent()
            ).use { it.executeUpdate() }
            for (item in list) {
                connection.prepareStatement(
                    """
                    INSERT INTO TodoItem(
                        id, description, priority, isCompleted, createdDate, dueDate, changedDate
                    ) VALUES(
                        ?, ?, ?, ?, ?, ?, ?
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
                    statement.executeUpdate()
                }
            }
        }
    }
}