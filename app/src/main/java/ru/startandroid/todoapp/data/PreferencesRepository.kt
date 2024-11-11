package ru.startandroid.todoapp.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferencesRepository(
    context: Context
) {

    companion object {
        const val FILE_NAME = "Preferences_Store"
        const val PREFERENCE_NAME = "isInitialized"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    var isInitialized: Boolean
        get() = sharedPreferences.getBoolean(PREFERENCE_NAME, false)
        set(value) {
            sharedPreferences.edit { putBoolean(PREFERENCE_NAME, value) }
        }
}