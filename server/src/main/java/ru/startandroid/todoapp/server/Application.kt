package ru.startandroid.todoapp.server

import io.ktor.server.application.Application
import ru.startandroid.todoapp.server.plugins.configureRouting
import ru.startandroid.todoapp.server.plugins.configureSerialization

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    configureRouting()
    configureSerialization()
}