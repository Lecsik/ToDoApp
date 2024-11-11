package ru.startandroid.todoapp.server.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.JsonConvertException
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import ru.startandroid.todoapp.server.Repository
import ru.startandroid.todoapp.server.models.TodoItem

fun Application.configureRouting() {
    routing {
        route("/items") {
            get {
                call.respond(Repository().getAllItems())
            }

            post("/add") {
                try {
                    val list = call.receive<List<TodoItem>>()
                    Repository().setAllItems(list)
                    call.respond(HttpStatusCode.NoContent)
                } catch (ex: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest)
                } catch (ex: JsonConvertException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
    }
}