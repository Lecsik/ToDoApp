package ru.startandroid.todoapp.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.fatboyindustrial.gsonjodatime.Converters
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import ru.startandroid.todoapp.models.TodoItem


class TodoItemsStorage(context: Context) {
    companion object {
        const val STORAGE_NAME = "StorageName"
    }

    private val settings: SharedPreferences =
        context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE)

    private val gson: Gson = Converters.registerLocalDate(GsonBuilder()).create()
    private val listType = object : TypeToken<ArrayList<TodoItem>>() {}.type


    fun setItems(items: List<TodoItem>) {
        settings.edit {
            putString("items", gson.toJson(items))

        }
    }

    fun getItems(): List<TodoItem> =
        gson.fromJson(settings.getString("items", "[]")!!, listType)


}