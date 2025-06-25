package ru.startandroid.todoapp.presentation.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance
import ru.startandroid.todoapp.data.TodoItemsRepository
import ru.startandroid.todoapp.data.api.PreferencesRepository
import ru.startandroid.todoapp.data.api.ServerException
import ru.startandroid.todoapp.models.Error

class LoginViewModel(application: Application) : AndroidViewModel(application), DIAware {
    override val di by closestDI()
    private val repository: TodoItemsRepository by di.instance()
    private val preferencesRepository: PreferencesRepository by di.instance()
    private val token: String? = preferencesRepository.userToken
    private val operationPrivate = MutableLiveData<Operation?>(null)
    val operation: LiveData<Operation?> get() = operationPrivate

    private val errorsPrivate = MutableLiveData<List<Error>>(emptyList())
    val errors: LiveData<List<Error>> get() = errorsPrivate

    private val donePrivate = MutableLiveData(false)
    val done: LiveData<Boolean> get() = donePrivate

    val login = MutableLiveData("")
    val password = MutableLiveData("")

    enum class Operation {
        LOADING
    }

    fun checkAuth(): Boolean {
        return token != null
    }

    fun closeServerError() {
        errorsPrivate.value = errorsPrivate.value!!
            .filterNot { it is Error.ServerError }
            .filterNot { it is Error.UnknownError }
    }

    fun setLogin(value: String) {
        login.value = value
        errorsPrivate.value = errorsPrivate.value!!
            .filterNot { it is Error.LoginEmpty }
            .filterNot { it is Error.UserNotExists }
            .filterNot { it is Error.WrongPassword }
    }

    fun setPassword(value: String) {
        password.value = value
        errorsPrivate.value = errorsPrivate.value!!
            .filterNot { it is Error.WrongPassword }

    }

    fun loginUser() {
        if (login.value.isNullOrBlank()) {
            errorsPrivate.value = errorsPrivate.value!! + Error.LoginEmpty
        } else {
            errorsPrivate.value = errorsPrivate.value!!.filterNot { it is Error.LoginEmpty }
        }

        if (errorsPrivate.value!!.isNotEmpty()) return

        operationPrivate.value = Operation.LOADING
        viewModelScope.launch {
            try {
                repository.login(login.value.toString(), password.value.toString())
                donePrivate.value = true
            } catch (e: ServerException) {
                if (e.errorKey == "LOGIN_NOT_FOUND") {
                    errorsPrivate.value = errorsPrivate.value!! + Error.UserNotExists
                } else if (e.errorKey == "WRONG_PASSWORD") {
                    errorsPrivate.value = errorsPrivate.value!! + Error.WrongPassword
                } else if (e.errorKey != null) {
                    errorsPrivate.value =
                        errorsPrivate.value!! + Error.ServerError(e.errorKey)
                } else {
                    errorsPrivate.value = errorsPrivate.value!! + Error.UnknownError
                }
            } catch (e: Exception) {
                errorsPrivate.value = errorsPrivate.value!! + Error.UnknownError
            } finally {
                operationPrivate.value = null
            }
        }
    }
}