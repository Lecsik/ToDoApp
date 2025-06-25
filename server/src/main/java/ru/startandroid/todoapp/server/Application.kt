package ru.startandroid.todoapp.server

import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.bearer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import ru.startandroid.todoapp.server.plugins.configureRouting
import ru.startandroid.todoapp.server.plugins.configureSerialization

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(Authentication) {
            bearer {
                authenticate { tokenCredential ->
                    val userId =
                        Repository().findUser(tokenCredential.token) ?: return@authenticate null
                    UserIdPrincipal(userId.toString())
                }
            }
        }

        configureRouting()
        configureSerialization()
    }.start(wait = true)
}
