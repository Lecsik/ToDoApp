package ru.startandroid.todoapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import org.joda.time.LocalDate
import ru.startandroid.todoapp.models.TodoItem

class TodoItemsRepository(private val database: TodoItemDatabase, private val api: TodoItemApi) {

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
        if (mutex.isLocked) mutex.unlock()
    }

    suspend fun removeItem(id: String) = withContext(Dispatchers.IO) {
        api.deleteItem(id)
        if (mutex.isLocked) mutex.unlock()
    }

    suspend fun setCompleted(id: String, isCompleted: Boolean) {
        val item = api.getItem(id)
        addItem(item.copy(isCompleted = isCompleted, changedDate = LocalDate.now()))
    }
}
