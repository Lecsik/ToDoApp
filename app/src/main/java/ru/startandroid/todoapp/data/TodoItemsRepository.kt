package ru.startandroid.todoapp.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.room.RoomDatabase
import io.reactivex.rxjava3.core.Completable
import org.joda.time.LocalDate
import ru.startandroid.todoapp.models.TodoItem


class TodoItemsRepository {

    companion object {
        val INSTANCE = TodoItemsRepository()
    }

    private lateinit var database: TodoItemDatabase

    fun init(context: Context) {
        database = Room.databaseBuilder(
            context,
            TodoItemDatabase::class.java,
            "database.db"
        ).allowMainThreadQueries().build()
    }

    val itemsLiveData: LiveData<List<TodoItem>> by lazy { database.todoItemDao.getAllTasks() }

    fun addItem(item: TodoItem): Completable = database.todoItemDao.upsert(item)

    fun removeItem(id: String): Completable = database.todoItemDao.delete(id)

    fun setCompleted(id: String, isCompleted: Boolean): Completable =
        database.todoItemDao.getTask(id)
            .flatMapCompletable { item ->
                addItem(item.copy(isCompleted = isCompleted, changedDate = LocalDate.now()))
            }
}
