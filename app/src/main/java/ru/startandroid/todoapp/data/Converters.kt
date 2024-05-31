package ru.startandroid.todoapp.data

import androidx.room.TypeConverter
import org.joda.time.LocalDate
import ru.startandroid.todoapp.models.TodoItem

internal object Converters {
    @TypeConverter
    @JvmStatic
    fun localDateToString(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    @JvmStatic
    fun stringToLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    @JvmStatic
    fun priorityToInt(value: TodoItem.Priority?): Int? = value?.ordinal

    @TypeConverter
    @JvmStatic
    fun intToPriority(value: Int?): TodoItem.Priority? =
        value?.let { TodoItem.Priority.entries[it] }

}