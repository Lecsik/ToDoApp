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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    value = LocalContentColor provides if (item.isCompleted) MaterialTheme.colorScheme.onSurface.copy(
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

            var openAlertDialog by remember { mutableStateOf(false) }
            IconButton(
                onClick = { openAlertDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = stringResource(id = R.string.close_button),
                    tint = MaterialTheme.colorScheme.error
                )
            }
            if (openAlertDialog) {
                DeleteAlertDialog(
                    onDismissRequest = { openAlertDialog = false },
                    onConfirmation = {
                        openAlertDialog = false
                        onRemove(index)
                    },
                    dialogTitle = stringResource(id = R.string.delete_dialog_title),
                    dialogText = stringResource(id = R.string.delete_dialog_text)
                )
            }
        }
    }
}

@Composable
fun DeleteAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(dialogTitle, lineHeight = 30.sp) },
        text = { Text(dialogText) },
        confirmButton = {
            TextButton(
                onClick = onConfirmation
            ) {
                Text(stringResource(id = R.string.delete_label))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
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
        id = "11112",
        description = "Jetpack Compose is a modern toolkit for building native Android UI. Jetpack Compose simplifies and accelerates UI development on Android with less code, powerful tools, and intuitive Kotlin APIs.",
        priority = TodoItem.Priority.LOW,
        isCompleted = true,
        createdDate = LocalDate.now(),
        dueDate = LocalDate(1996, 4, 4),
        changedDate = null
    )
    MyTheme {
        ItemCard(
            item = item,
            onCheckedChange = {},
            onRemove = {},
            onClick = {},
            index = 3,
        )
    }
}

@Preview
@Composable
fun ListCardPreviewPriorityHigh() {
    val item = TodoItem(
        id = "11113",
        description = "Jetpack Compose",
        priority = TodoItem.Priority.HIGH,
        isCompleted = false,
        createdDate = LocalDate.now(),
        dueDate = null,
        changedDate = null
    )
    MyTheme {
        ItemCard(
            item = item,
            onCheckedChange = {},
            onRemove = {},
            onClick = {},
            index = 2
        )
    }
}

@Preview
@Composable
fun ListCardPreviewPriorityHigh1sdgdsfg() {
    val item = TodoItem(
        id = "11112",
        description = "Jetpack Compose is a modern toolkit for building native Android UI. Jetpack Compose simplifies and accelerates UI development on Android with less code, powerful tools, and intuitive Kotlin APIs.",
        priority = TodoItem.Priority.HIGH,
        isCompleted = false,
        createdDate = LocalDate.now(),
        dueDate = LocalDate(1996, 4, 4),
        changedDate = null
    )
    MyTheme {
        ItemCard(
            item = item,
            onCheckedChange = {},
            onRemove = {},
            onClick = {},
            index = 4
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ListCardPreviewPriorityHigh1sdgds() {
    val item = TodoItem(
        id = "11112",
        description = "Jetpack Compose is a modern toolkit for building native Android UI. Jetpack Compose simplifies and accelerates UI development on Android with less code, powerful tools, and intuitive Kotlin APIs.",
        priority = TodoItem.Priority.HIGH,
        isCompleted = false,
        createdDate = LocalDate.now(),
        dueDate = LocalDate(1996, 4, 4),
        changedDate = null
    )
    MyTheme {
        ItemCard(
            item = item,
            onCheckedChange = {},
            onRemove = {},
            onClick = {},
            index = 4
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ListCardPreviewNight() {
    val item = TodoItem(
        id = "11111",
        description = "1111111",
        priority = TodoItem.Priority.LOW,
        isCompleted = true,
        createdDate = LocalDate.now(),
        dueDate = null,
        changedDate = null
    )
    MyTheme {
        ItemCard(
            item = item,
            onCheckedChange = {},
            onRemove = {},
            onClick = {},
            index = 5
        )
    }
}