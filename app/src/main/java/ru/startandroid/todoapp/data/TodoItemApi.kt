package ru.startandroid.todoapp.data

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.startandroid.todoapp.models.TodoItem

interface TodoItemApi {

    @GET("items")
    suspend fun getAllItems(): List<TodoItem>

    @DELETE("items/{id}")
    suspend fun deleteItem(@Path("id") id: String)

    @GET("items/{id}")
    suspend fun getItem(@Path("id") id: String): TodoItem

    @POST("/items")
    suspend fun addItem(@Body item: TodoItem)

}
