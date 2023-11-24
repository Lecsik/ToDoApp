package ru.startandroid.todoapp.presentation.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.joda.time.LocalDate
import ru.startandroid.todoapp.data.TodoItemsRepository
import ru.startandroid.todoapp.models.TodoItem
import java.util.UUID

class TaskViewModel : ViewModel() {
    private val repository = TodoItemsRepository.INSTANCE

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

    private val compositeDisposable = CompositeDisposable()

    enum class Operation {
        LOADING
    }

    fun remove(): String {
        operationPrivate.value = Operation.LOADING
        val existingItem = existingItem
        check(existingItem != null) { "No existing item" }
        repository.removeItem(existingItem.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                operationPrivate.value = null
                donePrivate.value = true
            }.let(compositeDisposable::add)
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
        repository.addItem(todoItem)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                operationPrivate.value = null
                donePrivate.value = true
            }.let(compositeDisposable::add)
        return todoItem
    }

    override fun onCleared() {
        compositeDisposable.dispose()
    }
}