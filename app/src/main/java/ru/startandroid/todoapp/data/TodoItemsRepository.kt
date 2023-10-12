package ru.startandroid.todoapp.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import org.joda.time.LocalDate
import ru.startandroid.todoapp.models.TodoItem


class TodoItemsRepository {

    companion object {
        val INSTANCE = TodoItemsRepository()
    }

    private lateinit var storage: TodoItemsStorage

    fun init(context: Context) {
        storage = TodoItemsStorage(
            Room.databaseBuilder(
                context,
                TodoItemDatabase::class.java,
                "database.db"
            ).allowMainThreadQueries().build()
        )
        itemsMutableLiveData.value = storage.getItems()
    }

    private val itemsMutableLiveData = MutableLiveData<List<TodoItem>>(emptyList())
    val itemsLiveData: LiveData<List<TodoItem>> get() = itemsMutableLiveData

    fun addItem(item: TodoItem) {
        val existingIndex = itemsLiveData.value!!.indexOfFirst { it.id == item.id }
        if (existingIndex == -1) storage.addItem(item)
        else storage.updateItem(item)
        itemsMutableLiveData.value = storage.getItems()
    }

    fun removeItem(id: String) {
        storage.removeItem(id)
        itemsMutableLiveData.value = storage.getItems()
    }

    fun setCompleted(id: String, isCompleted: Boolean) {
        val item = itemsLiveData.value!!.first { it.id == id }
        addItem(item.copy(isCompleted = isCompleted, changedDate = LocalDate.now()))
    }
}
