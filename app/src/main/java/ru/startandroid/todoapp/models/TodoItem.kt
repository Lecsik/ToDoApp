package ru.startandroid.todoapp.models

import android.os.Parcel
import android.os.Parcelable
import org.joda.time.LocalDate

data class TodoItem(
    val id: String,
    val description: String,
    val priority: Priority,
    val isCompleted: Boolean,
    val createdDate: LocalDate,
    val dueDate: LocalDate?,
    val changedDate: LocalDate?
) : Parcelable {

    enum class Priority {
        NONE,
        LOW,
        HIGH
    }

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        Priority.values()[parcel.readInt()],
        parcel.readInt() != 0,
        LocalDate.parse(parcel.readString())!!,
        parcel.readString()?.let { LocalDate.parse(it) },
        parcel.readString()?.let { LocalDate.parse(it) }
    )

    override fun describeContents(): Int {
        //  val string = Priority.NONE.toString()
        //  val enum = Priority.valueOf(string)
        //  val int = Priority.NONE.ordinal
        //  Priority.values()[int]
        return 0
    }

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

