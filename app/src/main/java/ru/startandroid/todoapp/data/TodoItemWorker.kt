package ru.startandroid.todoapp.data

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.*
import androidx.work.CoroutineWorker
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance

class TodoItemWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), DIAware {

    override val di by closestDI(context)
    private val todoItemApi: TodoItemApi by di.instance()
    private val database: TodoItemDatabase by di.instance()
    private val repository: TodoItemsRepository by di.instance()
    private val todoItemsLiveData = repository.itemsLiveData

    override suspend fun doWork(): Result {
        todoItemsLiveData.asFlow().collect {
            try {
                todoItemApi.setAllItems(database.todoItemDao.getAllTasks())
            } catch (_: Exception) {
            }
        }
        return Result.success()
    }
}