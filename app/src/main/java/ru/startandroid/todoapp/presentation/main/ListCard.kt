package ru.startandroid.todoapp.presentation.main

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.joda.time.LocalDate
import ru.startandroid.todoapp.R
import ru.startandroid.todoapp.models.TodoItem
import ru.startandroid.todoapp.ui.theme.MyTheme

@Composable
fun ItemCard(
    item: TodoItem,
    onCheckedChange: (Boolean) -> Unit,
    onRemove: (Int) -> Unit,
    onClick: () -> Unit,
    index: Int,
    modifier: Modifier = Modifier
) {
    val borderColor: Color = when (item.priority) {
        TodoItem.Priority.HIGH -> colorResource(R.color.priority_high)
        TodoItem.Priority.LOW -> colorResource(R.color.priority_low)
        TodoItem.Priority.NONE -> colorResource(R.color.priority_none)
    }
    OutlinedCard(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, borderColor),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
        ) {
            Checkbox(checked = item.isCompleted, onCheckedChange = onCheckedChange)

            Column(modifier = Modifier.weight(1.0f)) {
                val decoration: TextDecoration = when (item.isCompleted) {
                    true -> TextDecoration.LineThrough
                    false -> TextDecoration.None
                }

                CompositionLocalProvider(
                    LocalContentColor provides if (item.isCompleted) MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.38f
                    ) else MaterialTheme.colorScheme.onSurface
                ) {
                    Text(
                        text = item.description,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = decoration
                    )
                }
                if (item.dueDate != null) {
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = item.dueDate.toString("dd MMM YYYY"),
                        maxLines = 1,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            val openAlertDialog = remember { mutableStateOf(false) }
            IconButton(
                onClick = { openAlertDialog.value = true }
            ) {
                Icon(
                    Icons.Default.Clear,
                    stringResource(id = R.string.close_button),
                    tint = MaterialTheme.colorScheme.error
                )
            }
            if (openAlertDialog.value) {
                DeleteAlertDialog(
                    onDismissRequest = { openAlertDialog.value = false },
                    onConfirmation = {
                        openAlertDialog.value = false
                        onRemove(index)
                    },
                    dialogTitle = stringResource(id = R.string.delete_dialog_title),
                    dialogText = stringResource(id = R.string.delete_dialog_text)
                )
            }
        }
    }
}

/*@Composable
fun DismissBackground(dismissState: SwipeToDismissBoxState) {
    val color = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> colorResource(R.color.delete)
        SwipeToDismissBoxValue.EndToStart -> colorResource(R.color.done)
        SwipeToDismissBoxValue.Settled -> Color.Transparent
    }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(12.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(Icons.Default.Delete, contentDescription = stringResource(id = R.string.right_swipe))
        Spacer(modifier = Modifier)
        Icon(Icons.Default.Done, contentDescription = stringResource(id = R.string.left_swipe))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListCard(
    item: TodoItem,
    listIndex: Int,
    modifier: Modifier = Modifier,
    onRemove: (Int) -> Unit,
    onSetCompleted: (Int, Boolean) -> Unit
) {
    val context = LocalContext.current
    val currentItem by rememberUpdatedState(item)
    val currentIndex by rememberUpdatedState(newValue = listIndex)

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onRemove(currentIndex)
                    Toast.makeText(
                        context,
                        context.getString(R.string.right_swipe),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                SwipeToDismissBoxValue.EndToStart -> {
                    onSetCompleted(currentIndex, currentItem.isCompleted)
                    Toast.makeText(
                        context,
                        context.getString(R.string.left_swipe),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                SwipeToDismissBoxValue.Settled -> return@rememberSwipeToDismissBoxState false
            }
            return@rememberSwipeToDismissBoxState true
        },
        // positional threshold of 25%
        positionalThreshold = { it * .25f }
    )
    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        backgroundContent = { DismissBackground(dismissState) },
        content = {
            ItemCard(
                isChecked = item.isCompleted,
                onCheckedChange = { onSetCompleted(listIndex, item.isCompleted) },
                onRemove = { onRemove(listIndex) },
                index = listIndex,
                description = item.description,
                dueDate = item.dueDate,
                priority = item.priority
            )
        })
}*/

@Composable
fun DeleteAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text(dialogTitle) },
        text = { Text(dialogText) },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(stringResource(id = R.string.delete_label))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(id = R.string.cancel_label))
            }
        }
    )
}


@Preview
@Composable
fun ListCardPreview() {
    val item = TodoItem(
        "11112",
        "Jetpack Compose is a modern toolkit for building native Android UI. Jetpack Compose simplifies and accelerates UI development on Android with less code, powerful tools, and intuitive Kotlin APIs.",
        TodoItem.Priority.LOW,
        true,
        LocalDate.now(),
        LocalDate(1996, 4, 4),
        null
    )
    MyTheme {
        ItemCard(
            item,
            {},
            {},
            {},
            3,
        )
    }
}

@Preview
@Composable
fun ListCardPreviewPriorityHigh() {
    val item = TodoItem(
        "11113",
        "Jetpack Compose",
        TodoItem.Priority.HIGH,
        false,
        LocalDate.now(),
        null,
        null
    )
    MyTheme {
        ItemCard(
            item,
            {},
            {},
            {},
            2
        )
    }
}

@Preview
@Composable
fun ListCardPreviewPriorityHigh1sdgdsfg() {
    val item = TodoItem(
        "11112",
        "Jetpack Compose is a modern toolkit for building native Android UI. Jetpack Compose simplifies and accelerates UI development on Android with less code, powerful tools, and intuitive Kotlin APIs.",
        TodoItem.Priority.HIGH,
        false,
        LocalDate.now(),
        LocalDate(1996, 4, 4),
        null
    )
    MyTheme {
        ItemCard(
            item,
            {},
            {},
            {},
            4
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ListCardPreviewPriorityHigh1sdgds() {
    val item = TodoItem(
        "11112",
        "Jetpack Compose is a modern toolkit for building native Android UI. Jetpack Compose simplifies and accelerates UI development on Android with less code, powerful tools, and intuitive Kotlin APIs.",
        TodoItem.Priority.HIGH,
        false,
        LocalDate.now(),
        LocalDate(1996, 4, 4),
        null
    )
    MyTheme {
        ItemCard(
            item,
            {},
            {},
            {},
            4
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ListCardPreviewNight() {
    val item = TodoItem(
        "11111",
        "1111111",
        TodoItem.Priority.LOW,
        true,
        LocalDate.now(),
        null,
        null
    )
    MyTheme {
        ItemCard(
            item,
            {},
            {},
            {},
            5
        )
    }
}