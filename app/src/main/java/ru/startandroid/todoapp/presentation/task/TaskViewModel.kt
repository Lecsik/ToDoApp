package ru.startandroid.todoapp.presentation.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    fun removeItem() {
        val existingItem = existingItem
        check(existingItem != null) { "No existing item" }
        repository.removeItem(existingItem.id)
    }

    fun newItem() {
        existingItem?.let { item ->
            repository.addItem(
                item.copy(
                    dueDate = dueDate.value,
                    description = description.value!!,
                    priority = priority.value!!,
                    changedDate = LocalDate.now()
                )
            )
        } ?: run {
            val todoItem = TodoItem(
                UUID.randomUUID().toString(),
                description.value!!,
                priority.value!!,
                false,
                LocalDate.now(),
                dueDate.value,
                null
            )
            repository.addItem(todoItem)
        }
    }
}