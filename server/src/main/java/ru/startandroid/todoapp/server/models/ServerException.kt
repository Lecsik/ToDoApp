package ru.startandroid.todoapp.server.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServerException(

    @SerialName("error_key")
    val errorKey: String?,

    @SerialName("error_description")
    val errorDescription: String?,
) : Throwable(errorDescription)
