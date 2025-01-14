package ru.startandroid.todoapp.data

import androidx.lifecycle.LiveData
import org.joda.time.LocalDate
import ru.startandroid.todoapp.models.TodoItem


class TodoItemsRepository(
    private val database: TodoItemDatabase,
    private val api: TodoItemApi,
    private val preferencesRepository: PreferencesRepository
) {
    val itemsLiveData: LiveData<List<TodoItem>> = database.todoItemDao.getAllTasksLD()

    suspend fun checkInitialized() {
        if (!preferencesRepository.isInitialized) {
            database.todoItemDao.upsert(api.getAllItems())
            preferencesRepository.isInitialized = true
        }
    }

    suspend fun addItem(item: TodoItem) {
        database.todoItemDao.upsert(item)
    }

    suspend fun removeItem(id: String) {
        database.todoItemDao.delete(id)
    }

    suspend fun setCompleted(id: String, isCompleted: Boolean) {
        val item = database.todoItemDao.getTask(id)
        addItem(item.copy(isCompleted = isCompleted, changedDate = LocalDate.now()))
    }
}
