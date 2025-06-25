package ru.startandroid.todoapp.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.startandroid.todoapp.models.TodoItem

interface TodoItemApi {

    @POST("/register")
    suspend fun register(@Query("login") login: String, @Query("password") password: String): String

    @GET("/login")
    suspend fun login(@Query("login") login: String, @Query("password") password: String): String

    @GET("/items")
    suspend fun getAllItems(): List<TodoItem>

    @POST("/items/add")
    suspend fun addItem(@Body todoItem: TodoItem)

    @POST("/items/{id}/delete")
    suspend fun deleteItem(@Path("id") todoItemId: String)

    @GET("/items/{id}")
    suspend fun getItem(@Path("id") todoItemId: String): TodoItem

}
