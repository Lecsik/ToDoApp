package ru.startandroid.todoapp.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferencesRepository(
    context: Context
) {

    companion object {
        const val FILE_NAME = "Preferences_Store"

        const val PREFERENCE_TOKEN = "userToken"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    var userToken: String?
        get() = sharedPreferences.getString(PREFERENCE_TOKEN, null)
        set(value) {
            sharedPreferences.edit { putString(PREFERENCE_TOKEN, value) }
        }

    fun clear() {
        sharedPreferences.edit { clear() }
    }
}