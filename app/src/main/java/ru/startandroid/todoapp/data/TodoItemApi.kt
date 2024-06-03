package ru.startandroid.todoapp.data


import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.startandroid.todoapp.models.TodoItem

interface TodoItemApi {

    companion object {
        val INSTANCE by lazy {
            Retrofit.Builder()
                .baseUrl("http://192.168.1.20:8080/")
                .addConverterFactory(Json.asConverterFactory(MediaType.get("application/json")))
                .build()
                .create<TodoItemApi>()
        }
    }

    @GET("items")
    suspend fun getAllItems(): List<TodoItem>

    @DELETE("items/{id}")
    suspend fun deleteItem(@Path("id") id: String)

    @GET("items/{id}")
    suspend fun getItem(@Path("id") id: String): TodoItem

    @POST("/items")
    suspend fun addItem(@Body item: TodoItem)


}
