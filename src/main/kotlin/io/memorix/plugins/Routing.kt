package io.memorix.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.swagger.*
import io.memorix.messages.ErrorDetails
import io.memorix.messages.ErrorTypes
import io.memorix.routes.user
import io.swagger.codegen.v3.generators.html.StaticHtmlCodegen

fun Application.configureRouting() {
    install(AutoHeadResponse)
    install(Resources)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            when (cause) {
                /* Respond with BadRequest status if message body is malformed. */
                is BadRequestException ->
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = ErrorDetails(error = ErrorTypes.UNPARSEABLE_MESSAGE.errorDetail)
                    )
                /* Respond in all other cases with InternalServerError. */
                else -> call.respondText(text = "500: ${cause}", status = HttpStatusCode.InternalServerError)
            }
        }
    }
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        user()
        swaggerUI(path = "swagger", swaggerFile = "swagger_docs.yaml")
        openAPI(path="openapi", swaggerFile = "swagger_docs.yaml") {
            codegen = StaticHtmlCodegen()
        }
    }
}
