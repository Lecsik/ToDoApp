package ru.startandroid.todoapp.data.api

import okhttp3.Interceptor
import okhttp3.Response


class AuthInterceptor(private val preferencesRepository: PreferencesRepository) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        preferencesRepository.userToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        return chain.proceed(requestBuilder.build())
    }
}