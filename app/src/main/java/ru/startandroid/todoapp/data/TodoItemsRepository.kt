package ru.startandroid.todoapp.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import org.joda.time.LocalDate
import ru.startandroid.todoapp.models.TodoItem


class TodoItemsRepository {

    companion object {
        val INSTANCE = TodoItemsRepository()
    }

    private lateinit var database: TodoItemDatabase
    private val api = TodoItemApi.INSTANCE

    fun init(context: Context) {
        database = Room.databaseBuilder(
            context,
            TodoItemDatabase::class.java,
            "database.db"
        ).build()
    }

    private val mutex = Mutex()

    val itemsLiveData: LiveData<List<TodoItem>> = liveData {
        while (true) {
            mutex.lock()
            val value = api.getAllItems()
            if (value != latestValue) emit(value)
        }
    }

    suspend fun addItem(item: TodoItem) = withContext(Dispatchers.IO) {
        api.addItem(item)
        mutex.unlock()
    }

    suspend fun removeItem(id: String) = withContext(Dispatchers.IO) {
        api.deleteItem(id)
        mutex.unlock()
    }

    suspend fun setCompleted(id: String, isCompleted: Boolean) {
        val item = api.getItem(id)
        addItem(item.copy(isCompleted = isCompleted, changedDate = LocalDate.now()))
    }
}
