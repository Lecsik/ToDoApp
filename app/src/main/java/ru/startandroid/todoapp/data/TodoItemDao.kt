package ru.startandroid.todoapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import ru.startandroid.todoapp.models.TodoItem


@Dao
interface TodoItemDao {

    @Query("DELETE FROM TodoItem WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM TodoItem")
    suspend fun deleteAllTasks()

    @Upsert
    suspend fun upsert(todoItems: List<TodoItem>)

    @Upsert
    suspend fun upsert(todoItem: TodoItem)

    @Insert
    suspend fun setAllTasks(list: List<TodoItem>)

    @Query("SELECT * FROM TodoItem")
    fun getAllTasksLD(): LiveData<List<TodoItem>>

    @Query("SELECT * FROM TodoItem")
    suspend fun getAllTasks(): List<TodoItem>

    @Query("SELECT * FROM TodoItem WHERE id = :id")
    suspend fun getTask(id: String): TodoItem
}