package ru.startandroid.todoapp.presentation.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.startandroid.todoapp.R
import ru.startandroid.todoapp.models.Error
import ru.startandroid.todoapp.presentation.ErrorDialog
import ru.startandroid.todoapp.presentation.errorDescription
import ru.startandroid.todoapp.presentation.main.LoginDestination
import ru.startandroid.todoapp.presentation.main.MainDestination
import ru.startandroid.todoapp.ui.theme.MyTheme


class RegistrationScreen(private val navController: NavController) {
    @Composable
    fun Content() {
        val viewModel = viewModel<RegistrationViewModel>()
        val loginState by viewModel.login.observeAsState("")
        val passwordFirstState by viewModel.passwordFirst.observeAsState("")
        val passwordSecondState by viewModel.passwordSecond.observeAsState("")

        val done by viewModel.done.observeAsState(false)
        val operation by viewModel.operation.observeAsState()
        val errors by viewModel.errors.observeAsState(emptyList())

        RegistrationPresentation(
            onBackClick = {
                navController.popBackStack()
            },
            loginState = loginState,
            setLogin = { login -> viewModel.setLogin(login) },
            passwordFirst = passwordFirstState,
            passwordSecond = passwordSecondState,
            setPasswordFirst = { password -> viewModel.setPasswordFirst(password) },
            setPasswordSecond = { password -> viewModel.setPasswordSecond(password) },
            errors = errors,
            closeServerError = { viewModel.closeServerError() },
            registerUser = { viewModel.registerUser() }
        )

        if (done) {
            navController.navigate(MainDestination) {
                popUpTo<LoginDestination> { inclusive = true }
            }
        }
        if (operation == RegistrationViewModel.Operation.LOADING) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                LinearProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    @Composable
    fun RegistrationPresentation(
        onBackClick: () -> Unit,
        loginState: String,
        setLogin: (String) -> Unit,
        passwordFirst: String,
        passwordSecond: String,
        setPasswordFirst: (String) -> Unit,
        setPasswordSecond: (String) -> Unit,
        errors: List<Error>,
        closeServerError: () -> Unit,
        registerUser: () -> Unit,
    ) {
        var passwordVisibility by rememberSaveable { mutableStateOf(false) }

        Scaffold(
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(64.dp))

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp),
                        text = stringResource(R.string.registration_welcome),
                        fontSize = 26.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start
                    )
                    CompositionLocalProvider(value = LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                        TextButton(
                            onClick = {
                                onBackClick()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 1.dp)
                                .padding(horizontal = 5.dp),
                            contentPadding = PaddingValues(3.dp)
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    append(stringResource(R.string.have_account))
                                    withStyle(
                                        style = SpanStyle(fontWeight = FontWeight.Bold)
                                    ) {
                                        append(stringResource(R.string.log_in))
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = loginState,
                        onValueChange = {
                            if (!it.contains(' ') && !it.contains("\t"))
                                setLogin(it)
                        },
                        placeholder = {
                            Text(text = stringResource(R.string.login))
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                        ),
                        supportingText = {
                            when {
                                errors.contains(Error.LoginEmpty) -> {
                                    Text(
                                        text = stringResource(R.string.empty_login),
                                        color = colorResource(R.color.delete),
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                errors.contains(Error.UserExists) -> {
                                    Text(
                                        text = stringResource(R.string.registration_error),
                                        color = colorResource(R.color.delete),
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        },
                        isError = errors.contains(Error.LoginEmpty)
                                || errors.contains(Error.UserExists),
                        maxLines = 1,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        ),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = passwordFirst,
                        onValueChange = {
                            setPasswordFirst(it)
                        },
                        placeholder = { Text(stringResource(R.string.password)) },
                        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        supportingText = {
                            when {
                                errors.contains(Error.ShortPassword) -> {
                                    Text(
                                        text = stringResource(R.string.validation_password_error),
                                        color = colorResource(R.color.delete),
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        },
                        isError = errors.contains(Error.ShortPassword),
                        trailingIcon = {
                            IconButton(
                                onClick = { passwordVisibility = !passwordVisibility },
                                content = {
                                    if (passwordVisibility)
                                        Icon(
                                            painter = painterResource(id = R.drawable.visibility),
                                            contentDescription = stringResource(id = R.string.visibility_button),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    else Icon(
                                        painter = painterResource(id = R.drawable.visibility_off),
                                        contentDescription = stringResource(id = R.string.visibility_button),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                })
                        },
                        maxLines = 1,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = passwordSecond,
                        onValueChange = {
                            setPasswordSecond(it)
                        },
                        placeholder = { Text(stringResource(R.string.password)) },
                        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        supportingText = {
                            when {
                                errors.contains(Error.PasswordsNotEqual) -> {
                                    Text(
                                        text = stringResource(R.string.password_match_error),
                                        color = colorResource(R.color.delete),
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        },
                        isError = errors.contains(Error.PasswordsNotEqual),
                        trailingIcon = {
                            IconButton(
                                onClick = { passwordVisibility = !passwordVisibility },
                                content = {
                                    if (passwordVisibility)
                                        Icon(
                                            painter = painterResource(id = R.drawable.visibility),
                                            contentDescription = stringResource(id = R.string.visibility_button),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    else Icon(
                                        painter = painterResource(id = R.drawable.visibility_off),
                                        contentDescription = stringResource(id = R.string.visibility_button),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                })
                        },
                        maxLines = 1,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { registerUser() },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            text = stringResource(R.string.sign_up_button),
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp
                        )
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
        )
    }

    @Suppress("ComposePreviewMustBeTopLevelFunction")
    @Preview
    @Composable
    fun RegistrationPresentationPreview() {
        MyTheme {
            RegistrationPresentation(
                onBackClick = {},
                loginState = "",
                setLogin = {},
                passwordFirst = "",
                passwordSecond = "",
                setPasswordFirst = {},
                setPasswordSecond = {},
                errors = emptyList(),
                closeServerError = {},
                registerUser = {},
            )
        }
    }

}