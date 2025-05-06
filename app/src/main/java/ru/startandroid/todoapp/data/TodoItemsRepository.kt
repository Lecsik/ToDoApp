package ru.startandroid.todoapp.data

import android.util.Log
import org.joda.time.LocalDate
import retrofit2.HttpException
import ru.startandroid.todoapp.models.TodoItem


class TodoItemsRepository(
    private val api: TodoItemApi,
    private val preferencesRepository: PreferencesRepository
) {
    suspend fun register(login: String, password: String): Boolean {
        return try {
            preferencesRepository.userToken = api.register(login, password)
            true
        } catch (exception: HttpException) {
            false
        }
    }

    suspend fun login(login: String, password: String): Boolean {
        return try {
            preferencesRepository.userToken = api.login(login, password)
            true
        } catch (exception: HttpException) {
            false
        }
    }

    suspend fun addItem(item: TodoItem) {
        try {
            api.addItem(item)
        } catch (exception: HttpException) {
            Log.d("server response", "Unauthorized")
        }
    }

    suspend fun removeItem(id: String) {
        try {
            api.deleteItem(id)
        } catch (exception: HttpException) {
            Log.d("server response", "Unauthorized")
        }
    }

    suspend fun setCompleted(id: String, isCompleted: Boolean) {
        try {
            val item = api.getItem(id)
            addItem(item.copy(isCompleted = isCompleted, changedDate = LocalDate.now()))
        } catch (exception: HttpException) {
            Log.d("server response", "Unauthorized")
        }
    }

    suspend fun getAllItems(): List<TodoItem> {
        try {
            return api.getAllItems()
        } catch (exception: HttpException) {
            Log.d("server response", "Unauthorized")
            return emptyList()
        }
    }
}
