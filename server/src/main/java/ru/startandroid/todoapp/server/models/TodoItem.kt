package ru.startandroid.todoapp.server.models

import kotlinx.serialization.Serializable
import org.joda.time.LocalDate
import ru.startandroid.todoapp.server.LocalDateSerializer

@Serializable
data class TodoItem(
    val id: String,
    val description: String,
    val priority: Priority,
    val isCompleted: Boolean,
    @Serializable(LocalDateSerializer::class) val createdDate: LocalDate,
    @Serializable(LocalDateSerializer::class) val dueDate: LocalDate?,
    @Serializable(LocalDateSerializer::class) val changedDate: LocalDate?
) {

    enum class Priority {
        NONE,
        LOW,
        HIGH
    }
}
