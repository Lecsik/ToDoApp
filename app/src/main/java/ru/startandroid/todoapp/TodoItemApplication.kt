package ru.startandroid.todoapp

import android.app.Application
import ru.startandroid.todoapp.data.TodoItemsRepository

class TodoItemApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val repository = TodoItemsRepository.INSTANCE
        repository.init(this)
    }
}