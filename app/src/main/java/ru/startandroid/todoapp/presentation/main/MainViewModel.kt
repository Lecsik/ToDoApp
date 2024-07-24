package ru.startandroid.todoapp.presentation.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance
import ru.startandroid.todoapp.data.TodoItemsRepository
import ru.startandroid.todoapp.models.TodoItem

class MainViewModel(application: Application) : AndroidViewModel(application), DIAware {

    override val di by closestDI()

    private val repository: TodoItemsRepository by di.instance()

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
        viewModelScope.launch {
            repository.removeItem(items.value!![position].id)
        }
    }

    fun setCompleted(position: Int, isCompleted: Boolean) {
        operationPrivate.value = Operation.LOADING
        viewModelScope.launch {
            repository.setCompleted(items.value!![position].id, isCompleted)
        }
    }

}
