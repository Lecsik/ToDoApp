package ru.startandroid.todoapp.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import ru.startandroid.todoapp.data.TodoItemsRepository
import ru.startandroid.todoapp.models.TodoItem
import kotlin.concurrent.thread


class MainViewModel : ViewModel() {

    private val repository = TodoItemsRepository.INSTANCE

    private val isCompletedTasksVisiblePrivate = MutableLiveData(false)
    val isCompletedTasksVisible: LiveData<Boolean> get() = isCompletedTasksVisiblePrivate

    private val operationPrivate = MutableLiveData(Operation.LOADING)
    val operation: LiveData<Operation?> get() = operationPrivate

    enum class Operation {
        LOADING
    }

    fun switchCompletedTasksVisibility() {
        isCompletedTasksVisiblePrivate.value = isCompletedTasksVisiblePrivate.value!!.not()
    }

    private val itemsPrivate = MediatorLiveData<List<TodoItem>>().apply {
        addSource(repository.itemsLiveData) { items ->
            value =
                if (isCompletedTasksVisible.value == true) items
                else items.filter { it.isCompleted.not() }
            operationPrivate.value = null
        }
        addSource(isCompletedTasksVisible) { isCompletedTasksVisible ->
            val items = repository.itemsLiveData.value ?: return@addSource
            value =
                if (isCompletedTasksVisible == true) items
                else items.filter { it.isCompleted.not() }
            operationPrivate.value = null
        }
    }
    val items: LiveData<List<TodoItem>> get() = itemsPrivate

    val count: LiveData<Int> = repository.itemsLiveData.map { it.count { it.isCompleted } }

    fun removeItem(position: Int) {
        operationPrivate.value = Operation.LOADING
        thread { repository.removeItem(items.value!![position].id) }
    }

    fun setCompleted(position: Int, isCompleted: Boolean) {
        operationPrivate.value = Operation.LOADING
        thread { repository.setCompleted(items.value!![position].id, isCompleted) }
    }
}
