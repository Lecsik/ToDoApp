package ru.startandroid.todoapp.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import ru.startandroid.todoapp.models.TodoItem

interface TodoItemApi {

    @GET("/items")
    suspend fun getAllItems(): List<TodoItem>

    @POST("/items/add")
    suspend fun setAllItems(@Body list: List<TodoItem>)
}
