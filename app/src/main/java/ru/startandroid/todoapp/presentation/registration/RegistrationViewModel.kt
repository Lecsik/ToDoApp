package ru.startandroid.todoapp.presentation.registration

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
import ru.startandroid.todoapp.data.api.ServerException
import ru.startandroid.todoapp.models.Error

class RegistrationViewModel(application: Application) : AndroidViewModel(application), DIAware {
    override val di by closestDI()

    private val repository: TodoItemsRepository by di.instance()

    private val operationPrivate = MutableLiveData<Operation?>(null)
    val operation: LiveData<Operation?> get() = operationPrivate

    private val errorsPrivate = MutableLiveData<List<Error>>(emptyList())
    val errors: LiveData<List<Error>> get() = errorsPrivate

    private val donePrivate = MutableLiveData(false)
    val done: LiveData<Boolean> get() = donePrivate

    val login = MutableLiveData("")
    val passwordFirst = MutableLiveData("")
    val passwordSecond = MutableLiveData("")

    enum class Operation {
        LOADING
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
            .filterNot { it is Error.UserExists }
    }

    fun setPasswordFirst(value: String) {
        passwordFirst.value = value
        errorsPrivate.value = errorsPrivate.value!!
            .filterNot { it is Error.ShortPassword }
            .filterNot { it is Error.PasswordsNotEqual }
    }

    fun setPasswordSecond(value: String) {
        passwordSecond.value = value
        errorsPrivate.value = errorsPrivate.value!!
            .filterNot { it is Error.PasswordsNotEqual }
    }

    fun registerUser() {
        if (login.value.isNullOrBlank()) {
            errorsPrivate.value = errorsPrivate.value!! + Error.LoginEmpty
        } else {
            errorsPrivate.value = errorsPrivate.value!!.filterNot { it is Error.LoginEmpty }
        }

        if (passwordFirst.value!!.length < 8) {
            errorsPrivate.value = errorsPrivate.value!! + Error.ShortPassword
        } else {
            errorsPrivate.value = errorsPrivate.value!!.filterNot { it is Error.ShortPassword }
        }

        if (passwordFirst.value != passwordSecond.value) {
            errorsPrivate.value = errorsPrivate.value!! + Error.PasswordsNotEqual
        } else {
            errorsPrivate.value = errorsPrivate.value!!.filterNot { it is Error.PasswordsNotEqual }
        }

        if (errorsPrivate.value!!.isNotEmpty()) return

        operationPrivate.value = Operation.LOADING
        viewModelScope.launch {
            try {
                repository.register(login.value.toString(), passwordSecond.value.toString())
                donePrivate.value = true
            } catch (e: ServerException) {
                if (e.errorKey == "USER_EXISTS") {
                    errorsPrivate.value = errorsPrivate.value!! + Error.UserExists
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
