package ru.startandroid.todoapp.presentation.registration

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
import ru.startandroid.todoapp.data.TodoItemsRepository

class RegistrationViewModel(application: Application) : AndroidViewModel(application), DIAware {
    override val di by closestDI()

    private val repository: TodoItemsRepository by di.instance()

    private val operationPrivate = MutableLiveData<Operation?>(null)
    val operation: LiveData<Operation?> get() = operationPrivate

    private val errorsPrivate = MutableLiveData<Errors?>(null)
    val errors: LiveData<Errors?> get() = errorsPrivate

    private val donePrivate = MutableLiveData(false)
    val done: LiveData<Boolean> get() = donePrivate

    val login = MutableLiveData("")
    val passwordFirst = MutableLiveData("")
    val passwordSecond = MutableLiveData("")


    enum class Operation {
        LOADING
    }

    enum class Errors {
        LOGIN_EMPTY,
        USER_FOUND,
        IS_SHORT,
        NOT_EQUALS
    }


    fun setLogin(value: String) {
        login.value = value
        errorsPrivate.value = null
    }

    fun setPasswordFirst(value: String) {
        passwordFirst.value = value
        errorsPrivate.value = null

    }

    fun setPasswordSecond(value: String) {
        passwordSecond.value = value
        errorsPrivate.value = null

    }

    fun registerUser() {
        if (login.value.isNullOrBlank()) {
            errorsPrivate.value = Errors.LOGIN_EMPTY
            operationPrivate.value = null
            return
        }
        if (passwordFirst.value != passwordSecond.value) {
            errorsPrivate.value = Errors.NOT_EQUALS
            operationPrivate.value = null
            return
        }
        passwordSecond.value?.let {
            if (it.length < 8) {
                errorsPrivate.value = Errors.IS_SHORT
                operationPrivate.value = null
                return@registerUser
            }
        }

        operationPrivate.value = Operation.LOADING
        viewModelScope.launch {
            try {
                val isNotExist =
                    repository.register(login.value.toString(), passwordSecond.value.toString())
                if (isNotExist) {
                    donePrivate.value = true
                } else errorsPrivate.value = Errors.USER_FOUND
            } catch (exception: IOException) {
                Log.d("server response", "some problem with server in register")
            }
            operationPrivate.value = null
        }
    }
}
