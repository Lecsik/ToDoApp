package ru.startandroid.todoapp.data.api

import android.os.Handler
import android.os.Looper
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.Response
import ru.startandroid.todoapp.presentation.main.LoginDestination
import ru.startandroid.todoapp.presentation.main.NavControllerHolder
import java.io.IOException

class ErrorsInterceptor(private val preferencesRepository: PreferencesRepository) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.isSuccessful.not()) {
            if (response.code == 401) {
                Handler(Looper.getMainLooper()).post {
                    preferencesRepository.userToken = null
                    NavControllerHolder.navController.get()?.navigate(LoginDestination) {
                        popUpTo<LoginDestination> { inclusive = true }
                    }
                }
                return response
            }
            runCatching {
                response.peekBody(Long.MAX_VALUE).string()
                    .let { Json.decodeFromString<ServerException>(it) }
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
