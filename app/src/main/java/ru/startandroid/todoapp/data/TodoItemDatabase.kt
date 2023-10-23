package ru.startandroid.todoapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.startandroid.todoapp.models.TodoItem

@Database(
    entities = [
        TodoItem::class,
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class TodoItemDatabase : RoomDatabase() {
    abstract val todoItemDao: TodoItemDao

}