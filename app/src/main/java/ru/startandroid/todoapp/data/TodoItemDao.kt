package ru.startandroid.todoapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ru.startandroid.todoapp.models.TodoItem


@Dao
interface TodoItemDao {

    @Query("DELETE FROM TodoItem WHERE id = :id")
    fun delete(id: String)

    @Upsert
    fun upsert(todoItem: TodoItem)

    @Query("SELECT * FROM TodoItem")
    fun getAllTasks(): LiveData<List<TodoItem>>

    @Query("SELECT * FROM TodoItem WHERE id = :id")
    fun getTask(id: String): TodoItem
}