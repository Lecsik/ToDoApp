package ru.startandroid.todoapp.presentation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import ru.startandroid.todoapp.R
import ru.startandroid.todoapp.models.Error

@Composable
fun errorDescription(
    error: Error.ServerError
): String {
    return when (error.key) {
        "LOGIN_TOO_SHORT" -> {
            stringResource(R.string.login_too_short)
        }

        "LOGIN_TOO_LONG" -> {
            stringResource(R.string.login_too_long)
        }

        "PASSWORD_TOO_SHORT" -> {
            stringResource(R.string.password_too_short)
        }

        "PASSWORD_TOO_LONG" -> {
            stringResource(R.string.login_too_long)
        }

        "USER_EXISTS" -> {
            stringResource(R.string.registration_error)
        }

        "LOGIN_NOT_FOUND" -> {
            stringResource(R.string.user_not_found)
        }

        "WRONG_PASSWORD" -> {
            stringResource(R.string.incorrect_password)
        }

        else -> {
            ""
        }
    }
}

@Composable
fun ErrorDialog(
    onDismissRequest: () -> Unit,
    errorDescription: String?
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                stringResource(R.string.error),
                lineHeight = 30.sp
            )
        },
        text = {
            if (!errorDescription.isNullOrEmpty()) {
                Text(errorDescription)
            } else Text(stringResource(R.string.server_problem))
        },
        confirmButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(id = R.string.ok))
            }
        },
    )
}