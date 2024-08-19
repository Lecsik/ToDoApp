package ru.startandroid.todoapp.models

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import org.joda.time.LocalDate
import ru.startandroid.todoapp.data.LocalDateSerializer

@Entity(primaryKeys = ["id"])
@Serializable
@Parcelize
data class TodoItem(
    val id: String,
    val description: String,
    val priority: Priority,
    val isCompleted: Boolean,
    @Serializable(LocalDateSerializer::class) val createdDate: LocalDate,
    @Serializable(LocalDateSerializer::class) val dueDate: LocalDate?,
    @Serializable(LocalDateSerializer::class) val changedDate: LocalDate?
) : Parcelable {
    enum class Priority {
        NONE,
        LOW,
        HIGH
    }
}

