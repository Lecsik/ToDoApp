package ru.startandroid.todoapp.presentation.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavController
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
import java.lang.ref.WeakReference

object NavControllerHolder {
    lateinit var navController: WeakReference<NavController>
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                val navController = rememberNavController()
                SideEffect {
                    NavControllerHolder.navController = WeakReference(navController)
                }
                NavHost(navController = navController, startDestination = LoginDestination) {
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
                                    id = itemList[0]!!,
                                    description = itemList[1]!!,
                                    priority = TodoItem.Priority.entries[itemList[2]!!.toInt()],
                                    isCompleted = itemList[3]!!.toInt() != 0,
                                    createdDate = itemList[4].let { LocalDate.parse(it) },
                                    dueDate = itemList[5]?.let { LocalDate.parse(it) },
                                    changedDate = itemList[6]?.let { LocalDate.parse(it) }
                                )
                            } else null
                            TaskScreen(navController = navController, item = item)
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