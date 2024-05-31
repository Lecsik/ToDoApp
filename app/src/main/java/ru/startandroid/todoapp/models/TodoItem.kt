package ru.startandroid.todoapp.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import kotlinx.serialization.Serializable
import org.joda.time.LocalDate
import ru.startandroid.todoapp.data.LocalDateSerializer

@Entity(primaryKeys = ["id"])
@Serializable
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

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        Priority.entries[parcel.readInt()],
        parcel.readInt() != 0,
        LocalDate.parse(parcel.readString())!!,
        parcel.readString()?.let { LocalDate.parse(it) },
        parcel.readString()?.let { LocalDate.parse(it) }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(description)
        writeInt(priority.ordinal)
        writeInt(if (isCompleted) 1 else 0)
        writeString(createdDate.toString())
        writeString(dueDate?.toString())
        writeString(changedDate?.toString())
    }

    companion object CREATOR : Parcelable.Creator<TodoItem> {
        override fun createFromParcel(parcel: Parcel): TodoItem {
            return TodoItem(parcel)
        }

        override fun newArray(size: Int): Array<TodoItem?> {
            return arrayOfNulls(size)
        }
    }
}

