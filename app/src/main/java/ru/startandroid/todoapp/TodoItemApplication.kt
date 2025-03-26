package ru.startandroid.todoapp

import android.app.Application
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import retrofit2.Retrofit
import retrofit2.create
import ru.startandroid.todoapp.data.AuthInterceptor
import ru.startandroid.todoapp.data.PreferencesRepository
import ru.startandroid.todoapp.data.TodoItemApi
import ru.startandroid.todoapp.data.TodoItemsRepository

class TodoItemApplication : Application(), DIAware {

    private fun apiModule(baseUrl: String) = DI.Module("apiModule") {
        bindSingleton {
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                .client(
                    OkHttpClient.Builder()
                        .addInterceptor(AuthInterceptor(instance()))
                        .addInterceptor(HttpLoggingInterceptor())
                        .build()
                )
                .build()
                .create<TodoItemApi>()
        }
    }

    override val di by DI.lazy {
        import(apiModule("http://192.168.1.20:8080/"))
        bindSingleton<TodoItemsRepository> {
            TodoItemsRepository(instance(), instance())
        }
        bindSingleton<PreferencesRepository> { PreferencesRepository(this@TodoItemApplication) }
    }

}