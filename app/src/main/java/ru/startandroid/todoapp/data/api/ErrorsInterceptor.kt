package ru.startandroid.todoapp.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ErrorsInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.isSuccessful.not()) {
            runCatching {
                response.body?.string()?.let { Json.decodeFromString<ServerException>(it) }
            }
                .getOrNull()
                ?.let { throw it }
        }
        return response
    }
}

@Serializable
data class ServerException(

    @SerialName("error_key")
    val errorKey: String?,

    @SerialName("error_description")
    val errorDescription: String?,
) : IOException(errorDescription)
