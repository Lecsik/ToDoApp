package ru.startandroid.todoapp.server.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.util.getOrFail
import ru.startandroid.todoapp.server.Repository
import ru.startandroid.todoapp.server.models.ServerException
import ru.startandroid.todoapp.server.models.TodoItem

fun Application.configureRouting() {
    val repository = Repository()

    routing {
        post("/register") {
            try {
                val login = call.queryParameters["login"] ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                val password = call.queryParameters["password"] ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                val token = repository.register(login, password)
                call.respond('"' + token + '"')
            } catch (se: ServerException) {
                call.respond(HttpStatusCode.BadRequest, se)
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
        get("/login") {
            try {
                val login = call.queryParameters["login"] ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val password = call.queryParameters["password"] ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val token = repository.authorization(login, password)
                call.respond('"' + token + '"')
            } catch (se: ServerException) {
                call.respond(HttpStatusCode.BadRequest, se)
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        authenticate {
            route("/items") {
                get {
                    val userId = call.principal<UserIdPrincipal>()?.name ?: run {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@get
                    }
                    try {
                        call.respond(repository.getAllItems(userId.toLong()))
                    } catch (se: ServerException) {
                        call.respond(HttpStatusCode.BadRequest, se)
                    } catch (ex: Exception) {
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                }

                post("/set") {
                    val userId = call.principal<UserIdPrincipal>()?.name ?: run {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@post
                    }
                    try {
                        val list = call.receive<List<TodoItem>>()
                        repository.setAllItems(list, userId.toLong())
                        call.respond(HttpStatusCode.NoContent)
                    } catch (se: ServerException) {
                        call.respond(HttpStatusCode.BadRequest, se)
                    } catch (ex: Exception) {
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                }

                post("/add") {
                    val userId = call.principal<UserIdPrincipal>()?.name ?: run {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@post
                    }
                    try {
                        val todoItem = call.receive<TodoItem>()
                        repository.addItem(todoItem, userId.toLong())
                        call.respond(HttpStatusCode.NoContent)
                    } catch (se: ServerException) {
                        call.respond(HttpStatusCode.BadRequest, se)
                    } catch (ex: Exception) {
                        call.respond(HttpStatusCode.InternalServerError)
                    }

                }
                post("/{id}/delete") {
                    call.principal<UserIdPrincipal>()?.name ?: run {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@post
                    }
                    try {
                        val todoItemId = call.parameters.getOrFail("id")
                        repository.deleteItem(todoItemId)
                        call.respond(HttpStatusCode.NoContent)
                    } catch (se: ServerException) {
                        call.respond(HttpStatusCode.BadRequest, se)
                    } catch (ex: Exception) {
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                }

                get("{id}") {
                    call.principal<UserIdPrincipal>()?.name ?: run {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@get
                    }
                    try {
                        val todoItemId = call.parameters.getOrFail("id")
                        call.respond(repository.getItem(todoItemId))
                    } catch (se: ServerException) {
                        call.respond(HttpStatusCode.BadRequest, se)
                    } catch (ex: Exception) {
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                }
            }
        }
    }
}