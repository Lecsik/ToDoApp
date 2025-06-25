package ru.startandroid.todoapp.presentation.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance
import ru.startandroid.todoapp.data.TodoItemsRepository
import ru.startandroid.todoapp.data.api.PreferencesRepository
import ru.startandroid.todoapp.data.api.ServerException
import ru.startandroid.todoapp.models.Error
import ru.startandroid.todoapp.models.TodoItem

class MainViewModel(application: Application) : AndroidViewModel(application), DIAware {

    override val di by closestDI()

    private val repository: TodoItemsRepository by di.instance()
    private val preferencesRepository: PreferencesRepository by di.instance()

    private val isCompletedTasksVisiblePrivate = MutableLiveData(false)
    val isCompletedTasksVisible: LiveData<Boolean> get() = isCompletedTasksVisiblePrivate

    private val operationPrivate = MutableLiveData(Operation.LOADING)
    val operation: LiveData<Operation?> get() = operationPrivate

    private val errorsPrivate = MutableLiveData<List<Error>>(emptyList())
    val errors: LiveData<List<Error>> get() = errorsPrivate

    enum class Operation {
        LOADING
    }

    fun closeServerError() {
        errorsPrivate.value = errorsPrivate.value!!
            .filterNot { it is Error.ServerError }
            .filterNot { it is Error.UnknownError }
    }

    fun exit() {
        operationPrivate.value = Operation.LOADING
        viewModelScope.launch {
            preferencesRepository.clear()
        }
        operationPrivate.value = null
    }

    fun switchCompletedTasksVisibility() {
        isCompletedTasksVisiblePrivate.value = isCompletedTasksVisiblePrivate.value!!.not()
    }

    private val allItems = MutableLiveData<List<TodoItem>>(emptyList())

    private val refreshPrivate = MutableLiveData(false)
    val refresh: LiveData<Boolean> get() = refreshPrivate

    fun refreshItems() {
        operationPrivate.value = Operation.LOADING
        viewModelScope.launch {
            try {
                allItems.value = repository.getAllItems()
            } catch (exception: ServerException) {
                errorsPrivate.value = exception.errorDescription?.let {
                    errorsPrivate.value!! + Error.ServerError(it)
                } ?: (errorsPrivate.value!! + Error.UnknownError)
            } catch (exception: Exception) {
                errorsPrivate.value = errorsPrivate.value!! + Error.UnknownError
            } finally {
                operationPrivate.value = null
            }
            delay(10L)
            refreshPrivate.value = false
        }
    }

    fun onPullRefresh() {
        refreshPrivate.value = true
        refreshItems()
    }

    private val itemsPrivate = MediatorLiveData<List<TodoItem>>().apply {
        addSource(allItems) { items ->
            value =
                if (isCompletedTasksVisible.value == true) items
                else items.filter { it.isCompleted.not() }
            operationPrivate.value = null
        }
        addSource(isCompletedTasksVisible) { isCompletedTasksVisible ->
            val items = allItems.value ?: return@addSource
            value =
                if (isCompletedTasksVisible == true) items
                else items.filter { it.isCompleted.not() }
            operationPrivate.value = null
        }
    }

    val items: LiveData<List<TodoItem>> get() = itemsPrivate

    val count: LiveData<Int> = allItems.map { it.count { item -> item.isCompleted } }

    fun removeItem(position: Int) {
        operationPrivate.value = Operation.LOADING
        viewModelScope.launch {
            try {
                repository.removeItem(items.value!![position].id)
            } catch (exception: ServerException) {
                errorsPrivate.value = exception.errorDescription?.let {
                    errorsPrivate.value!! + Error.ServerError(it)
                } ?: (errorsPrivate.value!! + Error.UnknownError)
            } catch (exception: Exception) {
                errorsPrivate.value = errorsPrivate.value!! + Error.UnknownError
            } finally {
                operationPrivate.value = null
            }
            refreshItems()
        }
    }

    fun setCompleted(position: Int, isCompleted: Boolean) {
        operationPrivate.value = Operation.LOADING
        viewModelScope.launch {
            try {
                repository.setCompleted(items.value!![position].id, isCompleted)
            } catch (exception: ServerException) {
                errorsPrivate.value = exception.errorKey?.let {
                    errorsPrivate.value!! + Error.ServerError(it)
                } ?: (errorsPrivate.value!! + Error.UnknownError)
            } catch (exception: Exception) {
                errorsPrivate.value = errorsPrivate.value!! + Error.UnknownError
            } finally {
                operationPrivate.value = null
            }
            refreshItems()
        }
    }
}
