package ru.startandroid.todoapp.presentation.task

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class TaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
            .add(android.R.id.content, TaskFragment())
            .commit()
    }
}

