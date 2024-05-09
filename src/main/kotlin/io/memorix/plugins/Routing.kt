package io.memorix.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.memorix.messages.ErrorTypes
import io.memorix.messages.Errors
import io.memorix.user.user

fun Application.configureRouting() {
    install(AutoHeadResponse)
    install(Resources)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            when (cause) {
                is BadRequestException ->
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = Errors(error = ErrorTypes.UNPARSABLE_MESSAGE.errorDetail)
                    )
            }
//            else -> call.respondText(text = "500: ${cause}", status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        user()
    }
}
