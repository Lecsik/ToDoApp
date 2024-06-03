package ru.startandroid.todoapp.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.LocalDate
import ru.startandroid.todoapp.server.models.TodoItem
import java.sql.DriverManager

class Repository {

    private val dbConnection =
        DriverManager.getConnection("jdbc:sqlite:server/build/database.db")

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

    suspend fun deleteItem(id: String) = withContext(Dispatchers.IO) {
        dbConnection.use { connection ->
            connection.prepareStatement("DELETE FROM TodoItem WHERE id = ?").use { statement ->
                statement.setString(1, id)
                statement.executeUpdate()
            }
        }
    }

    suspend fun getItem(id: String): TodoItem = withContext(Dispatchers.IO) {
        dbConnection.use { connection ->
            connection.prepareStatement("SELECT * FROM TodoItem WHERE id = ?").use { statement ->
                statement.setString(1, id)
                val result = statement.executeQuery()
                TodoItem(
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
    }

    suspend fun addItem(item: TodoItem) = withContext(Dispatchers.IO) {
        dbConnection.use { connection ->
            connection.prepareStatement(
                """
                    INSERT INTO TodoItem(
                        id, description, priority, isCompleted, createdDate, dueDate, changedDate
                    ) VALUES(
                        ?, ?, ?, ?, ?, ?, ?
                    ) ON CONFLICT(id) DO UPDATE SET description = ?, priority = ?, isCompleted = ?, dueDate = ?, changedDate = ?
                """.trimIndent()
            ).use { statement ->
                statement.setString(1, item.id)
                statement.setString(2, item.description)
                statement.setInt(3, item.priority.ordinal)
                statement.setInt(4, if (item.isCompleted) 1 else 0)
                statement.setString(5, item.createdDate.toString())
                statement.setString(6, item.dueDate?.toString())
                statement.setString(7, item.changedDate?.toString())
                statement.setString(8, item.description)
                statement.setInt(9, item.priority.ordinal)
                statement.setInt(10, if (item.isCompleted) 1 else 0)
                statement.setString(11, item.dueDate?.toString())
                statement.setString(12, item.changedDate?.toString())
                statement.executeUpdate()
            }
        }
    }
}