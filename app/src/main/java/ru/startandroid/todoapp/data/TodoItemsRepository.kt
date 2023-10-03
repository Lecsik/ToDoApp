package ru.startandroid.todoapp.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.joda.time.LocalDate
import ru.startandroid.todoapp.models.TodoItem


class TodoItemsRepository {

    companion object {
        val INSTANCE = TodoItemsRepository()
    }

    private lateinit var storage: TodoItemsStorage

    fun init(context: Context) {
        storage = TodoItemsStorage(context)
        itemsMutableLiveData.value = storage.getItems()
    }

    private val itemsMutableLiveData = MutableLiveData<List<TodoItem>>(emptyList())
    val itemsLiveData: LiveData<List<TodoItem>> get() = itemsMutableLiveData

    fun addItem(item: TodoItem) {
        val items = itemsLiveData.value!!.toMutableList()
        val existingIndex = items.indexOfFirst { it.id == item.id }
        if (existingIndex == -1) items.add(item)
        else items[existingIndex] = item

        itemsMutableLiveData.value = items
        storage.setItems(items)
    }

    fun removeItem(id: String) {
        val items = itemsLiveData.value!!.filter { it.id != id }
        // items.removeAll { it.id == id }
        itemsMutableLiveData.value = items
        storage.setItems(items)
    }

    fun setCompleted(id: String, isCompleted: Boolean) {
        val item = itemsLiveData.value!!.first { it.id == id }
        addItem(item.copy(isCompleted = isCompleted, changedDate = LocalDate.now()))
    }
}
