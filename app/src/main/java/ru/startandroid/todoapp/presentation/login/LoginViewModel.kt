package ru.startandroid.todoapp.presentation.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okio.IOException
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance
import ru.startandroid.todoapp.data.PreferencesRepository
import ru.startandroid.todoapp.data.ServerException
import ru.startandroid.todoapp.data.TodoItemsRepository

class LoginViewModel(application: Application) : AndroidViewModel(application), DIAware {
    override val di by closestDI()
    private val repository: TodoItemsRepository by di.instance()
    private val preferencesRepository: PreferencesRepository by di.instance()
    private val token: String? = preferencesRepository.userToken
    private val operationPrivate = MutableLiveData<Operation?>(null)
    val operation: LiveData<Operation?> get() = operationPrivate

    private val errorsPrivate = MutableLiveData<Errors?>(null)
    val errors: LiveData<Errors?> get() = errorsPrivate

    private val donePrivate = MutableLiveData(false)
    val done: LiveData<Boolean> get() = donePrivate

    val login = MutableLiveData("")
    val password = MutableLiveData("")

    enum class Operation {
        LOADING
    }

    enum class Errors {
        LOGIN_EMPTY,
        USER_NOT_FOUND
    }

    fun checkAuth(): Boolean {
        return token != null
    }

    fun setLogin(value: String) {
        login.value = value
        errorsPrivate.value = null
    }

    fun setPassword(value: String) {
        password.value = value
        errorsPrivate.value = null
    }

    fun loginUser() {
        operationPrivate.value = Operation.LOADING
        if (login.value.isNullOrBlank()) errorsPrivate.value = Errors.LOGIN_EMPTY
        viewModelScope.launch {
            if (errorsPrivate.value != Errors.LOGIN_EMPTY) {
                try {
                    val isExist =
                        repository.login(login.value.toString(), password.value.toString())
                    if (isExist) {
                        donePrivate.value = true
                    } else errorsPrivate.value = Errors.USER_NOT_FOUND
                } catch (exception: ServerException) {
                    when (exception.errorKey) {
                        "login_not_found" -> errorsPrivate.value = Errors.USER_NOT_FOUND
                        "login_is_empty" -> errorsPrivate.value = Errors.LOGIN_EMPTY
                        else -> {
                            // отобразить дефолтный диалог
                        }
                    }
                } catch (exception: IOException) {
                    Log.d("server response", "some problem with server in login")
                }
            }
            operationPrivate.value = null
        }
    }
}