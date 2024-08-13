package ru.startandroid.todoapp.presentation.task


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.findNavController
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate
import ru.startandroid.todoapp.R
import ru.startandroid.todoapp.models.TodoItem
import ru.startandroid.todoapp.ui.theme.MyTheme

class TaskFragment : Fragment() {

    companion object {
        const val RESULT_KEY = "TaskFragmentResultKey"
        const val RESULT_NEW_ITEM_KEY = "newItem"
        const val RESULT_DELETE_KEY = "deleteItem"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val resultBundle = bundleOf()
        val viewModel = ViewModelProvider(this).get<TaskViewModel>()

        TaskFragmentArgs.fromBundle(requireArguments()).todoItem
            ?.let { viewModel.setExistingItem(it) }

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val priority by viewModel.priority.observeAsState(TodoItem.Priority.NONE)
                val description by viewModel.description.observeAsState("")
                val dueDate by viewModel.dueDate.observeAsState()
                val done by viewModel.done.observeAsState(false)
                val isItemExists by viewModel.isItemExists.observeAsState(false)
                val operation by viewModel.operation.observeAsState()

                MyTheme {
                    TaskScreen(
                        onBack = { findNavController().navigateUp() },
                        onDelete = {
                            resultBundle.putString(
                                RESULT_DELETE_KEY,
                                viewModel.remove()
                            )
                        },
                        priority = priority,
                        description = description,
                        onDescription = { text -> viewModel.description.value = text },
                        dueDate = dueDate,
                        onChangeDueDate = { date: LocalDate? -> viewModel.dueDate.value = date },
                        onPriorityButton = { priority: TodoItem.Priority ->
                            viewModel.priority.value = priority
                        },
                        isItemExists = isItemExists,
                        onSaveItem = {
                            viewModel.description.value?.takeIf { it.isNotBlank() }?.let {
                                resultBundle.putParcelable(RESULT_NEW_ITEM_KEY, viewModel.save())
                            }
                        })
                }
                if (done) {
                    parentFragmentManager.setFragmentResult(RESULT_KEY, resultBundle)
                    requireView().findNavController().navigateUp()
                }
                if (operation == TaskViewModel.Operation.LOADING) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(color = MaterialTheme.colorScheme.background.copy(alpha = 0.8f)),
                        contentAlignment = Alignment.Center
                    ) {
                        LinearProgressIndicator(Modifier.align(Alignment.Center))
                    }
                }
            }
        }

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TaskScreen(
        onBack: () -> Unit,
        onDelete: () -> Unit,
        priority: TodoItem.Priority,
        description: String,
        onDescription: (String) -> Unit,
        dueDate: LocalDate?,
        onChangeDueDate: (LocalDate?) -> Unit,
        onPriorityButton: (TodoItem.Priority) -> Unit,
        isItemExists: Boolean,
        onSaveItem: () -> Unit
    ) {
        val scrollState = rememberScrollState()
        var state by remember { mutableStateOf(description) }
        var mDisplayMenu by remember { mutableStateOf(false) }
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text(
                            stringResource(id = R.string.task_screen_title),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.back_button)
                            )
                        }
                    },
                    actions = {
                        if (isItemExists) {
                            IconButton(
                                onClick = { mDisplayMenu = !mDisplayMenu }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = stringResource(id = R.string.menu_buton)
                                )
                            }
                            DropdownMenu(
                                expanded = mDisplayMenu,
                                onDismissRequest = { mDisplayMenu = false }) {
                                DropdownMenuItem(
                                    text = {
                                        Row {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = stringResource(
                                                    id = R.string.delete_label
                                                ),
                                                tint = colorResource(id = R.color.delete)
                                            )
                                            Spacer(modifier = Modifier.weight(1.0f))
                                            Text(stringResource(id = R.string.delete_label))
                                        }
                                    },
                                    onClick = onDelete,
                                    modifier = Modifier.wrapContentSize()
                                )
                            }
                        }
                    },

                    )
            },
        ) { innerPadding ->
            val colorPriority = when (priority) {
                TodoItem.Priority.NONE -> colorResource(id = R.color.priority_none)
                TodoItem.Priority.LOW -> colorResource(id = R.color.priority_low)
                TodoItem.Priority.HIGH -> colorResource(id = R.color.priority_high)
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedCard(
                    border = BorderStroke(2.dp, colorPriority),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                ) {
                    TextField(
                        value = state,
                        onValueChange = {
                            state = it
                            onDescription(state)
                        },
                        minLines = 5,
                        maxLines = 10,
                        placeholder = { Text(stringResource(id = R.string.description_text)) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth()
                ) {
                    var showDatePicker by remember { mutableStateOf(false) }
                    val deadline = rememberDatePickerState(
                        initialSelectedDateMillis = dueDate?.toDateTimeAtStartOfDay(DateTimeZone.UTC)?.millis
                    )
                    Text(
                        text = stringResource(id = R.string.deadline_label),
                        modifier = Modifier.weight(1.0f)
                    )
                    TextButton(
                        onClick = { showDatePicker = true }
                    ) {
                        Text(
                            text = if (dueDate == null) {
                                stringResource(id = R.string.null_date)
                            } else LocalDate(
                                deadline.selectedDateMillis,
                                DateTimeZone.UTC
                            ).toString("dd MMM YYYY")
                        )
                    }
                    if (showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                Row {
                                    if (dueDate != null) {
                                        TextButton(
                                            onClick = {
                                                onChangeDueDate(null)
                                                showDatePicker = false
                                            },
                                            Modifier.padding(horizontal = 12.dp)
                                        ) {
                                            Text(text = stringResource(R.string.delete_label))
                                        }

                                        Spacer(modifier = Modifier.weight(1.0f))
                                    }

                                    TextButton(
                                        onClick = { showDatePicker = false }
                                    ) {
                                        Text(text = stringResource(R.string.cancel_label))
                                    }

                                    TextButton(
                                        onClick = {
                                            onChangeDueDate(
                                                LocalDate(
                                                    deadline.selectedDateMillis,
                                                    DateTimeZone.UTC
                                                )
                                            )
                                            showDatePicker = false
                                        }
                                    ) {
                                        Text(text = stringResource(android.R.string.ok))
                                    }
                                }
                            }
                        ) {
                            DatePicker(state = deadline)
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    TodoItem.Priority.entries.forEachIndexed { index, entry ->
                        val contentColor = colorResource(
                            when (entry) {
                                TodoItem.Priority.NONE -> R.color.priority_none
                                TodoItem.Priority.LOW -> R.color.priority_low
                                TodoItem.Priority.HIGH -> R.color.priority_high
                            }
                        )
                        OutlinedButton(
                            onClick = { onPriorityButton(entry) },
                            Modifier
                                .weight(1.0f)
                                .padding(end = 8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                if (priority == entry) colorPriority else MaterialTheme.colorScheme.background,
                                if (priority == entry) Color.White else contentColor
                            ),
                            border = BorderStroke(2.dp, contentColor)
                        ) {
                            Text(text = stringArrayResource(id = R.array.priorityListItems)[index])
                        }
                    }
                }
                Spacer(Modifier.weight(1.0f))
                Button(
                    onClick = onSaveItem,
                    Modifier
                        .padding(horizontal = 8.dp, vertical = 20.dp)
                        .fillMaxWidth(),
                    enabled = state.isNotBlank()
                ) {
                    Text(text = stringResource(id = R.string.save_button))
                }
            }
        }
    }


    @Preview
    @Composable
    fun TaskScreenPreview() {
        MyTheme {
            TaskScreen(
                {},
                {},
                TodoItem.Priority.NONE,
                description = "description,description,descriptiondescription,description," +
                        "description,description,description,description,description," +
                        "description,descriptiondescription,description,description,description," +
                        "description,description,descriptiondescription,descriptiondescription," +
                        "description,description,description,description111111" +
                        "description,description,description,description,description," +
                        "description,descriptiondescription,description,description,description," +
                        "description,description,descriptiondescription,descriptiondescription," +
                        "description,description,description,description111111" +
                        "description,description,description,description111111" +
                        "description,description,description,description,description," +
                        "description,descriptiondescription,description,description,description," +
                        "description,description,descriptiondescription,descriptiondescription," +
                        "description,description,description,description111111",
                {},
                LocalDate.now(),
                {},
                {},
                true,
                {}
            )
        }
    }

    @Preview
    @Composable
    fun TaskScreenPreviewEmpty() {
        MyTheme {
            TaskScreen(
                {},
                {},
                TodoItem.Priority.HIGH,
                description = "",
                {},
                null,
                {},
                {},
                false,
                {}
            )
        }
    }
}