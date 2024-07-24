package ru.startandroid.todoapp.presentation.main

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import org.joda.time.LocalDate
import ru.startandroid.todoapp.R
import ru.startandroid.todoapp.models.TodoItem
import ru.startandroid.todoapp.presentation.task.TaskFragment
import ru.startandroid.todoapp.ui.theme.MyTheme

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel = ViewModelProvider(this).get<MainViewModel>()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MyTheme {
                    TodoItemListData(viewModel)
                }
            }
        }
    }

    @Composable
    fun TodoItemListData(viewModel: MainViewModel) {
        val switchCompletedTasksVisibility = { viewModel.switchCompletedTasksVisibility() }
        val isCompletedTasksVisible by viewModel.isCompletedTasksVisible.observeAsState()
        val itemsList by viewModel.items.observeAsState(emptyList())
        val countCompleted by viewModel.count.observeAsState()
        val onRemoveClick = { position: Int -> viewModel.removeItem(position) }
        val onSetCompleted =
            { position: Int, isCompleted: Boolean -> viewModel.setCompleted(position, isCompleted) }
        var items = itemsList
        parentFragmentManager.setFragmentResultListener(
            TaskFragment.RESULT_KEY,
            this
        ) { _, bundle ->
            bundle.getString(TaskFragment.RESULT_DELETE_KEY)?.let { id ->
                items = itemsList.filter { it.id != id }
            }
            val newItem: TodoItem? =
                if (Build.VERSION.SDK_INT >= 33) {
                    bundle.getParcelable(TaskFragment.RESULT_NEW_ITEM_KEY, TodoItem::class.java)
                } else bundle.getParcelable(TaskFragment.RESULT_NEW_ITEM_KEY)
            if (newItem != null) items = itemsList + newItem
        }
        if (countCompleted != null && isCompletedTasksVisible != null) {
            TodoItemListPresentation(
                switchCompletedTasksVisibility = switchCompletedTasksVisibility,
                isCompletedTasksVisible = isCompletedTasksVisible!!,
                list = items,
                countCompleted = countCompleted!!,
                onRemoveClick = onRemoveClick,
                onSetCompleted = onSetCompleted
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TodoItemListPresentation(
        switchCompletedTasksVisibility: () -> Unit,
        isCompletedTasksVisible: Boolean,
        list: List<TodoItem>,
        countCompleted: Int,
        onRemoveClick: (position: Int) -> Unit,
        onSetCompleted: (position: Int, isCompleted: Boolean) -> Unit
    ) {
        val scrollBehavior =
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    title = {
                        Column(Modifier.wrapContentHeight()) {
                            Text(stringResource(R.string.large_title))
                            Text(
                                text = stringResource(
                                    id = R.string.executed_title,
                                    countCompleted
                                ),

                                fontSize = 16.sp,
                                color = colorResource(id = R.color.label_tertiary)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = switchCompletedTasksVisibility) {
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
                    },
                    scrollBehavior = scrollBehavior,
                    expandedHeight = 130.dp
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    findNavController().navigate(MainFragmentDirections.actionMainFragmentToTaskFragment())
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        )
        { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(list.size, null, itemContent = { index ->
                    ItemCard(
                        list[index],
                        onCheckedChange = { onSetCompleted(index, !list[index].isCompleted) },
                        onRemove = { onRemoveClick(index) },
                        onClick = {
                            findNavController().navigate(
                                MainFragmentDirections.actionMainFragmentToTaskFragment(
                                    list[index]
                                )
                            )
                        },
                        index = index
                    )
                })
            }
        }
    }

    @Preview
    @Composable
    fun TodoItemListPreview() {
        MyTheme {
            TodoItemListPresentation(
                switchCompletedTasksVisibility = { },
                isCompletedTasksVisible = true,
                list = listOf(
                    TodoItem(
                        "11111",
                        "1111111",
                        TodoItem.Priority.LOW,
                        false,
                        LocalDate.now(),
                        null,
                        null
                    ),
                    TodoItem(
                        "11112",
                        "Jetpack Compose is a modern toolkit for building native Android UI. Jetpack Compose simplifies and accelerates UI development on Android with less code, powerful tools, and intuitive Kotlin APIs.",
                        TodoItem.Priority.LOW,
                        false,
                        LocalDate.now(),
                        LocalDate(1996, 4, 4),
                        null
                    ),
                    TodoItem(
                        "11113",
                        "Jetpack Compose",
                        TodoItem.Priority.HIGH,
                        true,
                        LocalDate.now(),
                        null,
                        null
                    ),
                    TodoItem(
                        "11114",
                        "View в Android",
                        TodoItem.Priority.HIGH,
                        false,
                        LocalDate.now(),
                        LocalDate(1996, 4, 4),
                        null
                    ),
                    TodoItem(
                        "11115",
                        "Многопоточность",
                        TodoItem.Priority.LOW,
                        true,
                        LocalDate.now(),
                        LocalDate(1996, 4, 4),
                        null
                    )
                ),
                countCompleted = 2,
                onRemoveClick = {},
                onSetCompleted = { _, _ -> }
            )
        }

    }


    @Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun TodoItemListNightPreview() {
        MyTheme {
            TodoItemListPresentation(
                switchCompletedTasksVisibility = { },
                isCompletedTasksVisible = true,
                list = listOf(
                    TodoItem(
                        "11111",
                        "1111111",
                        TodoItem.Priority.LOW,
                        false,
                        LocalDate.now(),
                        null,
                        null
                    ),
                    TodoItem(
                        "11112",
                        "Jetpack Compose is a modern toolkit for building native Android UI. Jetpack Compose simplifies and accelerates UI development on Android with less code, powerful tools, and intuitive Kotlin APIs.",
                        TodoItem.Priority.LOW,
                        false,
                        LocalDate.now(),
                        LocalDate(1996, 4, 4),
                        null
                    ),
                    TodoItem(
                        "11113",
                        "Jetpack Compose",
                        TodoItem.Priority.HIGH,
                        true,
                        LocalDate.now(),
                        null,
                        null
                    ),
                    TodoItem(
                        "11114",
                        "View в Android",
                        TodoItem.Priority.HIGH,
                        false,
                        LocalDate.now(),
                        LocalDate(1996, 4, 4),
                        null
                    ),
                    TodoItem(
                        "11115",
                        "Многопоточность",
                        TodoItem.Priority.LOW,
                        true,
                        LocalDate.now(),
                        LocalDate(1996, 4, 4),
                        null
                    )
                ),
                countCompleted = 2,
                onRemoveClick = {},
                onSetCompleted = { _, _ -> }
            )
        }
    }
}

