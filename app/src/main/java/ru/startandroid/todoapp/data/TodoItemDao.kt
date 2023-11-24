package ru.startandroid.todoapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.startandroid.todoapp.models.TodoItem


@Dao
interface TodoItemDao {

    @Query("DELETE FROM TodoItem WHERE id = :id")
    fun delete(id: String): Completable

    @Upsert
    fun upsert(todoItem: TodoItem): Completable

    @Query("SELECT * FROM TodoItem")
    fun getAllTasks(): LiveData<List<TodoItem>>

    @Query("SELECT * FROM TodoItem WHERE id = :id")
    fun getTask(id: String): Single<TodoItem>
}