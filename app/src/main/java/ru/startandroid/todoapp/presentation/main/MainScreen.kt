package ru.startandroid.todoapp.presentation.main


import android.content.res.Configuration
import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import org.joda.time.LocalDate
import ru.startandroid.todoapp.R
import ru.startandroid.todoapp.models.Error
import ru.startandroid.todoapp.models.TodoItem
import ru.startandroid.todoapp.presentation.ErrorDialog
import ru.startandroid.todoapp.presentation.errorDescription
import ru.startandroid.todoapp.ui.theme.MyTheme

@Parcelize
class MainScreen(private val navController: @RawValue NavController) : Parcelable {

    @Composable
    fun Content() {
        val viewModel = viewModel<MainViewModel>()
        val isCompletedTasksVisible by viewModel.isCompletedTasksVisible.observeAsState(false)
        val switchCompletedTasksVisibility = { viewModel.switchCompletedTasksVisibility() }
        val countCompleted by viewModel.count.observeAsState(0)
        val itemsList by viewModel.items.observeAsState(emptyList())
        val refresh by viewModel.refresh.observeAsState(false)
        val onPullRefresh = { viewModel.onPullRefresh() }
        val onRemoveClick = { position: Int -> viewModel.removeItem(position) }
        val onSetCompleted =
            { position: Int, isCompleted: Boolean -> viewModel.setCompleted(position, isCompleted) }
        val operation by viewModel.operation.observeAsState()
        val exit = {
            viewModel.exit()
            navController.navigate(LoginDestination) {
                popUpTo<MainDestination> { inclusive = true }
            }
        }
        val errors by viewModel.errors.observeAsState(emptyList())

        LaunchedEffect(Unit) {
            viewModel.refreshItems()
        }

        TodoItemListPresentation(
            exit = exit,
            isCompletedTasksVisible = isCompletedTasksVisible,
            onCompletedTasksVisibilityClick = switchCompletedTasksVisibility,
            countCompleted = countCompleted,
            list = itemsList,
            refresh = refresh,
            onPullRefresh = onPullRefresh,
            onRemoveClick = onRemoveClick,
            onSetCompleted = onSetCompleted,
            onItemAddClick = { navController.navigate(TaskDestination(null)) },
            onItemClick = {
                navController.navigate(
                    TaskDestination(
                        listOf(
                            it.id,
                            it.description,
                            it.priority.ordinal.toString(),
                            if (it.isCompleted) "1" else "0",
                            it.createdDate.toString(),
                            it.dueDate?.toString(),
                            it.changedDate?.toString()
                        )
                    )
                )
            },
            errors = errors,
            closeServerError = { viewModel.closeServerError() }
        )
        if (operation == MainViewModel.Operation.LOADING) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.8f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                LinearProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodoItemListPresentation(
    exit: () -> Unit,
    isCompletedTasksVisible: Boolean,
    onCompletedTasksVisibilityClick: () -> Unit,
    countCompleted: Int,
    list: List<TodoItem>,
    refresh: Boolean,
    onPullRefresh: () -> Unit,
    onRemoveClick: (position: Int) -> Unit,
    onSetCompleted: (position: Int, isCompleted: Boolean) -> Unit,
    onItemAddClick: () -> Unit,
    onItemClick: (TodoItem) -> Unit,
    errors: List<Error>,
    closeServerError: () -> Unit,
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    var mDisplayMenu by remember { mutableStateOf(false) }
    var openAlertDialog by remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = {
                    Column(modifier = Modifier.wrapContentHeight()) {
                        Text(text = stringResource(R.string.large_title))
                        Text(
                            text = stringResource(
                                id = R.string.executed_title,
                                countCompleted
                            ),

                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onCompletedTasksVisibilityClick,
                    ) {
                        if (isCompletedTasksVisible) {
                            Icon(
                                painter = painterResource(id = R.drawable.visibility),
                                contentDescription = stringResource(id = R.string.visibility_button),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        } else Icon(
                            painter = painterResource(id = R.drawable.visibility_off),
                            contentDescription = stringResource(id = R.string.visibility_button),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { mDisplayMenu = !mDisplayMenu }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = stringResource(id = R.string.menu_button),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    DropdownMenu(
                        expanded = mDisplayMenu,
                        onDismissRequest = { mDisplayMenu = false }) {
                        DropdownMenuItem(
                            text = {
                                Row {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                        contentDescription = stringResource(R.string.exit_app),
                                        tint = colorResource(id = R.color.delete)
                                    )
                                    Spacer(modifier = Modifier.weight(1.0f))
                                    Text(stringResource(id = R.string.exit_app))
                                }
                            },
                            onClick = { openAlertDialog = true },
                            modifier = Modifier.wrapContentSize()
                        )
                    }
                    if (openAlertDialog) {
                        AlertDialog(
                            onDismissRequest = { openAlertDialog = false },
                            title = {
                                Text(
                                    stringResource(R.string.exit_dialog_title),
                                    lineHeight = 30.sp
                                )
                            },
                            text = { Text(stringResource(R.string.exit_dialog_text)) },
                            confirmButton = {
                                TextButton(
                                    onClick = exit
                                ) {
                                    Text(stringResource(id = R.string.exit_app))
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { openAlertDialog = false }
                                ) {
                                    Text(stringResource(id = R.string.cancel_label))
                                }
                            }
                        )
                    }
                },
                expandedHeight = 130.dp,
                colors = TopAppBarDefaults.topAppBarColors(scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer),
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                shape = RoundedCornerShape(16.dp),
                onClick = onItemAddClick
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    )
    { innerPadding ->
        PullToRefreshBox(
            isRefreshing = refresh,
            onRefresh = onPullRefresh,
            modifier = Modifier.padding(innerPadding),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(list.size, null, itemContent = { index ->
                    ItemCard(
                        item = list[index],
                        onCheckedChange = { onSetCompleted(index, !list[index].isCompleted) },
                        onRemove = { onRemoveClick(index) },
                        onClick = { onItemClick(list[index]) },
                        index = index
                    )
                })
            }
        }

        val serverError = errors
            .filterIsInstance<Error.ServerError>()
            .firstOrNull()
        val unknownError = errors
            .filterIsInstance<Error.UnknownError>()
            .firstOrNull()
        serverError?.let {
            ErrorDialog(
                onDismissRequest = closeServerError,
                errorDescription = errorDescription(serverError)
            )
        }
        unknownError?.let {
            ErrorDialog(
                onDismissRequest = closeServerError,
                errorDescription = stringResource(R.string.server_problem)
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TodoItemListPreview() {
    MyTheme {
        TodoItemListPresentation(
            exit = {},
            isCompletedTasksVisible = true,
            onCompletedTasksVisibilityClick = { },
            countCompleted = 2,
            list = listOf(
                TodoItem(
                    id = "11111",
                    description = "1111111",
                    priority = TodoItem.Priority.LOW,
                    isCompleted = false,
                    createdDate = LocalDate.now(),
                    dueDate = null,
                    changedDate = null
                ),
                TodoItem(
                    id = "11112",
                    description = "Jetpack Compose is a modern toolkit for building native Android UI. Jetpack Compose simplifies and accelerates UI development on Android with less code, powerful tools, and intuitive Kotlin APIs.",
                    priority = TodoItem.Priority.LOW,
                    isCompleted = false,
                    createdDate = LocalDate.now(),
                    dueDate = LocalDate(1996, 4, 4),
                    changedDate = null
                ),
                TodoItem(
                    id = "11113",
                    description = "Jetpack Compose",
                    priority = TodoItem.Priority.HIGH,
                    isCompleted = true,
                    createdDate = LocalDate.now(),
                    dueDate = null,
                    changedDate = null
                ),
                TodoItem(
                    id = "11114",
                    description = "View в Android",
                    priority = TodoItem.Priority.HIGH,
                    isCompleted = false,
                    createdDate = LocalDate.now(),
                    dueDate = LocalDate(1996, 4, 4),
                    changedDate = null
                ),
                TodoItem(
                    id = "11115",
                    description = "Многопоточность",
                    priority = TodoItem.Priority.LOW,
                    isCompleted = true,
                    createdDate = LocalDate.now(),
                    dueDate = LocalDate(1996, 4, 4),
                    changedDate = null
                )
            ),
            refresh = false,
            onPullRefresh = {},
            onRemoveClick = {},
            onSetCompleted = { _, _ -> },
            onItemAddClick = {},
            onItemClick = {},
            errors = emptyList(),
            closeServerError = {},
        )
    }

}