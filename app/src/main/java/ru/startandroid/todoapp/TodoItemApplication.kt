package ru.startandroid.todoapp

import android.app.Application
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import retrofit2.Retrofit
import retrofit2.create
import ru.startandroid.todoapp.data.PreferencesRepository
import ru.startandroid.todoapp.data.TodoItemApi
import ru.startandroid.todoapp.data.TodoItemDatabase
import ru.startandroid.todoapp.data.TodoItemsRepository

@OptIn(DelicateCoroutinesApi::class)
class TodoItemApplication : Application(), DIAware {

    private fun dbModule(databaseFileName: String) = DI.Module("databaseModule") {
        bindSingleton {
            Room.databaseBuilder(
                this@TodoItemApplication,
                TodoItemDatabase::class.java,
                databaseFileName
            ).build()
        }
    }

    private fun apiModule(baseUrl: String) = DI.Module("apiModule") {
        bindSingleton {
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(Json.asConverterFactory(MediaType.get("application/json")))
                .build()
                .create<TodoItemApi>()
        }
    }

    override val di by DI.lazy {
        import(dbModule("database.db"))
        import(apiModule("http://192.168.1.20:8080/"))
        bindSingleton<TodoItemsRepository> {
            TodoItemsRepository(instance(), instance(), instance())
        }
        bindSingleton<PreferencesRepository> { PreferencesRepository(this@TodoItemApplication) }
    }

    override fun onCreate() {
        super.onCreate()

        val todoItemsRepository: TodoItemsRepository by di.instance()
        GlobalScope.launch {
            todoItemsRepository.checkInitialized()
        }
    }
}