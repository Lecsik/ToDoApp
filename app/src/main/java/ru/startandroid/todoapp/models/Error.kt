package ru.startandroid.todoapp.models

sealed interface Error {
    //authorization
    data object UserNotExists : Error
    data object WrongPassword : Error

    //registration
    data object ShortPassword : Error
    data object PasswordsNotEqual : Error
    data object UserExists : Error

    //auth/registr
    data object LoginEmpty : Error
    data class ServerError(val key: String) : Error

    //common
    data object UnknownError : Error
}