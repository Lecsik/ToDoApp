package ru.startandroid.todoapp.data

import org.joda.time.LocalDate
import ru.startandroid.todoapp.data.api.PreferencesRepository
import ru.startandroid.todoapp.data.api.TodoItemApi
import ru.startandroid.todoapp.models.TodoItem


class TodoItemsRepository(
    private val api: TodoItemApi,
    private val preferencesRepository: PreferencesRepository
) {
    suspend fun register(login: String, password: String) {
        preferencesRepository.userToken = api.register(login, password)
    }

    suspend fun login(login: String, password: String) {
        preferencesRepository.userToken = api.login(login, password)
    }

    suspend fun addItem(item: TodoItem) {
        api.addItem(item)
    }

    suspend fun removeItem(id: String) {
        api.deleteItem(id)
    }

    suspend fun setCompleted(id: String, isCompleted: Boolean) {
        val item = api.getItem(id)
        addItem(item.copy(isCompleted = isCompleted, changedDate = LocalDate.now()))
    }

    suspend fun getAllItems(): List<TodoItem> {
        return api.getAllItems()
    }
}
