package ru.startandroid.todoapp.presentation.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator
import ru.startandroid.todoapp.presentation.login.LoginScreen
import ru.startandroid.todoapp.ui.theme.MyTheme

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                val isAuthenticated = remember { false }
                Navigator(if (isAuthenticated) MainScreen() else LoginScreen())
            }
        }
    }
}
