package ru.startandroid.todoapp.data

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import org.joda.time.LocalDate
import ru.startandroid.todoapp.models.TodoItem


class TodoItemsStorage(private val database: SQLiteDatabase) {

    fun setItems(items: List<TodoItem>) {
        val delCount = database.delete("todoItems", null, null)
        Log.d("loggg", "deleted rows count = $delCount")
        items.forEach {
            val cv = ContentValues().apply {
                put("id", it.id)
                put("description", it.description)
                put("priority", it.priority.ordinal)
                put("isCompleted", if (it.isCompleted) 1 else 0)
                put("createdDate", it.createdDate.toString())
                put("dueDate", it.dueDate?.toString())
                put("changedDate", it.changedDate?.toString())
            }
            val rowID = database.insert("todoItems", null, cv)
            Log.d("loggg", "row inserted, ID = $rowID")
        }
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
            } else Log.d("loggg", "0 rows")
            resultList
        }
    }
}