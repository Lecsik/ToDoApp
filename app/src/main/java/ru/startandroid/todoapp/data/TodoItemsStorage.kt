package ru.startandroid.todoapp.data

import androidx.lifecycle.LiveData
import ru.startandroid.todoapp.models.TodoItem


class TodoItemsStorage(private val database: TodoItemDatabase) {

    fun addItem(item: TodoItem) {
        database.todoItemDao.insert(item)
    }

    fun removeItem(id: String) {
        database.todoItemDao.delete(id)
    }

    fun updateItem(item: TodoItem) {
        database.todoItemDao.update(item)
    }

    fun getItems(): LiveData<List<TodoItem>> {
        return database.todoItemDao.getAllTasks()
    }
}