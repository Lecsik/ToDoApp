package ru.startandroid.todoapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import androidx.room.Update
import ru.startandroid.todoapp.models.TodoItem


@Dao
interface TodoItemDao {
    @Insert
    fun insert(todoItem: TodoItem)

    @Update
    fun update(todoItem: TodoItem)

    @Query("DELETE FROM TodoItem WHERE id = :id")
    fun delete(id: String)

    @Query("SELECT * FROM TodoItem")
    fun getAllTasks(): List<TodoItem>
}