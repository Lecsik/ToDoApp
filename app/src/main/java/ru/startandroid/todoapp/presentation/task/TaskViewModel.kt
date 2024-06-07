package ru.startandroid.todoapp.presentation.task

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance
import ru.startandroid.todoapp.data.TodoItemsRepository
import ru.startandroid.todoapp.models.TodoItem
import java.util.UUID

class TaskViewModel(application: Application) : AndroidViewModel(application), DIAware {
    override val di by closestDI()
    private val repository: TodoItemsRepository by di.instance()

    private var existingItem: TodoItem? = null
    fun setExistingItem(item: TodoItem) {
        existingItem = item
        isItemExistsPrivate.value = true
        description.value = item.description
        dueDate.value = item.dueDate
        priority.value = item.priority
    }

    private val isItemExistsPrivate = MutableLiveData(false)
    val isItemExists: LiveData<Boolean> get() = isItemExistsPrivate

    val description = MutableLiveData("")

    val dueDate: MutableLiveData<LocalDate?> = MutableLiveData(null)

    val priority: MutableLiveData<TodoItem.Priority> = MutableLiveData(TodoItem.Priority.NONE)


    private val operationPrivate = MutableLiveData<Operation?>(null)
    val operation: LiveData<Operation?> get() = operationPrivate

    private val donePrivate = MutableLiveData(false)
    val done: LiveData<Boolean> get() = donePrivate


    enum class Operation {
        LOADING
    }

    fun remove(): String {
        operationPrivate.value = Operation.LOADING
        val existingItem = existingItem
        check(existingItem != null) { "No existing item" }
        viewModelScope.launch {
            repository.removeItem(existingItem.id)
            operationPrivate.value = null
            donePrivate.value = true
        }
        return existingItem.id
    }

    fun save(): TodoItem {
        operationPrivate.value = Operation.LOADING
        val todoItem = existingItem?.copy(
            dueDate = dueDate.value,
            description = description.value!!,
            priority = priority.value!!,
            changedDate = LocalDate.now()
        ) ?: TodoItem(
            UUID.randomUUID().toString(),
            description.value!!,
            priority.value!!,
            false,
            LocalDate.now(),
            dueDate.value,
            null
        )
        viewModelScope.launch {
            repository.addItem(todoItem)
            operationPrivate.value = null
            donePrivate.value = true
        }
        return todoItem
    }

}