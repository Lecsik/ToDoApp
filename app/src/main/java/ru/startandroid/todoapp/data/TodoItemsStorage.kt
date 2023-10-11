package ru.startandroid.todoapp.data

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import org.joda.time.LocalDate
import ru.startandroid.todoapp.models.TodoItem


class TodoItemsStorage(private val database: SQLiteDatabase) {

    fun addItem(item: TodoItem) {
        val cv = ContentValues().apply {
            put("id", item.id)
            put("description", item.description)
            put("priority", item.priority.ordinal)
            put("isCompleted", if (item.isCompleted) 1 else 0)
            put("createdDate", item.createdDate.toString())
            put("dueDate", item.dueDate?.toString())
            put("changedDate", item.changedDate?.toString())
        }
        database.insert("todoItems", null, cv)
    }

    fun removeItem(id: String) {
        database.delete("todoItems", "id = ?", arrayOf(id))
    }

    fun updateItem(item: TodoItem) {
        val cv = ContentValues().apply {
            put("id", item.id)
            put("description", item.description)
            put("priority", item.priority.ordinal)
            put("isCompleted", if (item.isCompleted) 1 else 0)
            put("createdDate", item.createdDate.toString())
            put("dueDate", item.dueDate?.toString())
            put("changedDate", item.changedDate?.toString())
        }
        database.update("todoItems", cv, "id = ?", arrayOf(item.id))
    }

    fun getItems(): List<TodoItem> {
        return database.query("todoItems", null, null, null, null, null, null).use { cursor ->
            val resultList: ArrayList<TodoItem> = arrayListOf()
            if (cursor.moveToFirst()) {
                val idColIndex = cursor.getColumnIndex("id")
                val descriptionColIndex = cursor.getColumnIndex("description")
                val priorityColIndex = cursor.getColumnIndex("priority")
                val isCompletedColIndex = cursor.getColumnIndex("isCompleted")
                val createdDateColIndex = cursor.getColumnIndex("createdDate")
                val dueDateColIndex = cursor.getColumnIndex("dueDate")
                val changedDateColIndex = cursor.getColumnIndex("changedDate")
                do {
                    resultList.add(TodoItem(
                        cursor.getString(idColIndex),
                        cursor.getString(descriptionColIndex),
                        TodoItem.Priority.values()[cursor.getInt(priorityColIndex)],
                        cursor.getInt(isCompletedColIndex) != 0,
                        LocalDate.parse(cursor.getString(createdDateColIndex)),
                        cursor.getString(dueDateColIndex)?.let { LocalDate.parse(it) },
                        cursor.getString(changedDateColIndex)?.let { LocalDate(it) }
                    ))
                } while (cursor.moveToNext())
            }
            resultList
        }
    }
}