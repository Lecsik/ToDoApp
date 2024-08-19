package ru.startandroid.todoapp.presentation.task

import android.os.Parcelable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.parcelize.Parcelize
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate
import ru.startandroid.todoapp.R
import ru.startandroid.todoapp.models.TodoItem
import ru.startandroid.todoapp.ui.theme.MyTheme

@Parcelize
class TaskScreen(private val item: TodoItem?) : Screen, Parcelable {

    @Composable
    override fun Content() {
        val viewModel = viewModel<TaskViewModel>()
        val navigator = LocalNavigator.currentOrThrow
        LaunchedEffect(Unit) {
            item?.let { viewModel.setExistingItem(it) }
        }
        val description by viewModel.description.observeAsState("")
        val dueDate by viewModel.dueDate.observeAsState()
        val priority by viewModel.priority.observeAsState(TodoItem.Priority.NONE)
        val done by viewModel.done.observeAsState(false)
        val isItemExists by viewModel.isItemExists.observeAsState(false)
        val operation by viewModel.operation.observeAsState()

        TaskScreenPresentation(
            onBackClick = { navigator.pop() },
            isItemExists = isItemExists,
            onDeleteClick = {
                viewModel.remove()
            },
            description = description,
            onDescriptionChange = { text -> viewModel.description.value = text },
            dueDate = dueDate,
            onDueDateChange = { date: LocalDate? -> viewModel.dueDate.value = date },
            priority = priority,
            onPriorityChange = { priorityEntry: TodoItem.Priority ->
                viewModel.priority.value = priorityEntry
            },
            onSave = {
                viewModel.description.value?.takeIf { it.isNotBlank() }?.let {
                    viewModel.save()
                }
            })
        if (done) {
            navigator.pop()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreenPresentation(
    onBackClick: () -> Unit,
    isItemExists: Boolean,
    onDeleteClick: () -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    dueDate: LocalDate?,
    onDueDateChange: (LocalDate?) -> Unit,
    priority: TodoItem.Priority,
    onPriorityChange: (TodoItem.Priority) -> Unit,
    onSave: () -> Unit
) {
    val scrollState = rememberScrollState()
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
                    IconButton(onClick = onBackClick) {
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
                                onClick = onDeleteClick,
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
                    value = description,
                    onValueChange = onDescriptionChange,
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
                val datePickerState = rememberDatePickerState(
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
                        text = dueDate?.toString("dd MMM YYYY")
                            ?: stringResource(id = R.string.null_date)
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
                                            onDueDateChange(null)
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
                                        onDueDateChange(
                                            LocalDate(
                                                datePickerState.selectedDateMillis,
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
                        DatePicker(state = datePickerState)
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
                        onClick = { onPriorityChange(entry) },
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
                onClick = onSave,
                Modifier
                    .padding(horizontal = 8.dp, vertical = 20.dp)
                    .fillMaxWidth(),
                enabled = description.isNotBlank()
            ) {
                Text(text = stringResource(id = R.string.save_button))
            }
        }
    }
}


@Preview
@Composable
fun TaskScreenPresentationPreview() {
    MyTheme {
        TaskScreenPresentation(
            {},
            true,
            {},
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
            TodoItem.Priority.NONE,
            {},
            {}
        )
    }
}

@Preview
@Composable
fun TaskScreenPresentationPreviewEmpty() {
    MyTheme {
        TaskScreenPresentation(
            {},
            false,
            {},
            description = "",
            {},
            null,
            {},
            TodoItem.Priority.HIGH,
            {},
            {}
        )
    }
}