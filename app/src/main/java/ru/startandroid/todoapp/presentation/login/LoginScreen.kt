package ru.startandroid.todoapp.presentation.login

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
import ru.startandroid.todoapp.presentation.main.RegistrationDestination
import ru.startandroid.todoapp.ui.theme.MyTheme

class LoginScreen(private val navController: NavController) {
    @Composable
    fun Content() {
        val viewModel = viewModel<LoginViewModel>()
        val loginState by viewModel.login.observeAsState("")
        val passwordState by viewModel.password.observeAsState("")
        val done by viewModel.done.observeAsState(false)
        val operation by viewModel.operation.observeAsState()
        val errors by viewModel.errors.observeAsState(emptyList())

        if (viewModel.checkAuth()) {
            navController.navigate(MainDestination) {
                popUpTo<LoginDestination> { inclusive = true }
            }
        }

        LoginPresentation(
            goToRegistrationScreen = {
                navController.navigate(RegistrationDestination)
            },
            login = loginState,
            setLogin = { login -> viewModel.setLogin(login) },
            password = passwordState,
            setPassword = { password -> viewModel.setPassword(password) },
            errors = errors,
            closeServerError = { viewModel.closeServerError() },
            loginUser = { viewModel.loginUser() },
        )

        if (done) {
            navController.navigate(MainDestination) {
                popUpTo<LoginDestination> { inclusive = true }
            }
        }
        if (operation == LoginViewModel.Operation.LOADING) {
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


    @Composable
    fun LoginPresentation(
        goToRegistrationScreen: () -> Unit,
        login: String,
        setLogin: (String) -> Unit,
        password: String,
        setPassword: (String) -> Unit,
        errors: List<Error>,
        closeServerError: () -> Unit,
        loginUser: () -> Unit,
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
                    horizontalAlignment = Alignment.Start
                ) {
                    Spacer(modifier = Modifier.height(96.dp))
                    Text(
                        modifier = Modifier
                            .padding(start = 8.dp),
                        text = stringResource(R.string.authorization_welcome),
                        fontSize = 26.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start
                    )

                    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                        TextButton(
                            onClick = goToRegistrationScreen,
                            modifier = Modifier
                                .defaultMinSize(minHeight = 1.dp)
                                .padding(horizontal = 5.dp), contentPadding = PaddingValues(3.dp)
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    append(stringResource(R.string.no_account))
                                    withStyle(
                                        style = SpanStyle(fontWeight = FontWeight.Bold)
                                    ) {
                                        append(stringResource(R.string.sign_up))
                                    }
                                },
                                modifier = Modifier
                                    .padding(0.dp),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = login,
                        onValueChange = {
                            if (!it.contains(' ') && !it.contains("\t"))
                                setLogin(it)
                        },
                        placeholder = {
                            Text(text = stringResource(R.string.login))
                        },
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

                                errors.contains(Error.UserNotExists) -> {
                                    Text(
                                        text = stringResource(R.string.user_not_found),
                                        color = colorResource(R.color.delete),
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        },
                        isError = errors.contains(Error.LoginEmpty)
                                || errors.contains(Error.UserNotExists),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                        ),
                        maxLines = 1,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        ),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = password,
                        onValueChange = {
                            setPassword(it)
                        },
                        placeholder = { Text(stringResource(R.string.password)) },
                        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        supportingText = {
                            if (errors.contains(Error.WrongPassword)) {
                                Text(
                                    text = stringResource(R.string.incorrect_password),
                                    color = colorResource(R.color.delete),
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        isError = errors.contains(Error.WrongPassword),
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
                        onClick = {
                            loginUser()
                        },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            text = stringResource(R.string.log_in),
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
    fun LoginPresentationPreview(
    ) {
        MyTheme {
            LoginPresentation(
                goToRegistrationScreen = {},
                login = "",
                setLogin = {},
                password = "",
                setPassword = {},
                errors = emptyList(),
                closeServerError = {},
                loginUser = {},
            )
        }
    }
}