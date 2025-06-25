package ru.startandroid.todoapp.presentation.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import org.joda.time.LocalDate
import ru.startandroid.todoapp.models.TodoItem
import ru.startandroid.todoapp.presentation.login.LoginScreen
import ru.startandroid.todoapp.presentation.registration.RegistrationScreen
import ru.startandroid.todoapp.presentation.task.TaskScreen
import ru.startandroid.todoapp.ui.theme.MyTheme

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                val isAuthenticated = remember { false }
                val startDestination: Any =
                    if (isAuthenticated) MainDestination else LoginDestination
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = startDestination) {
                    composable<LoginDestination> {
                        val screen = remember { LoginScreen(navController) }
                        screen.Content()
                    }
                    composable<RegistrationDestination> {
                        val screen = remember {
                            RegistrationScreen(navController)
                        }
                        screen.Content()
                    }
                    composable<MainDestination> {
                        val screen = remember {
                            MainScreen(navController)
                        }
                        screen.Content()
                    }

                    composable<TaskDestination> { backStackEntry ->
                        val screen = remember {
                            val itemList = backStackEntry.toRoute<TaskDestination>().itemList
                            val item = if (itemList != null) {
                                TodoItem(
                                    itemList[0]!!,
                                    itemList[1]!!,
                                    TodoItem.Priority.entries[itemList[2]!!.toInt()],
                                    itemList[3]!!.toInt() != 0,
                                    itemList[4].let { LocalDate.parse(it) },
                                    itemList[5]?.let { LocalDate.parse(it) },
                                    itemList[6]?.let { LocalDate.parse(it) }
                                )
                            } else null
                            TaskScreen(navController, item)
                        }
                        screen.Content()
                    }


                }
            }
        }
    }
}

@Serializable
data object LoginDestination

@Serializable
data object RegistrationDestination

@Serializable
data object MainDestination

@Serializable
data class TaskDestination(val itemList: List<String?>? = null)