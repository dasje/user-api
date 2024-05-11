package io.memorix.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.memorix.messages.ErrorTypes
import io.memorix.messages.NewUser
import io.memorix.messages.OutgoingMessage
import io.memorix.user.UserRepository
import org.koin.ktor.ext.inject

fun Route.user() {
    val repository: UserRepository by inject()

    route(Routes.USERS_ROUTE) {

         get {
             val queryValue = call.request.queryParameters["query"]
             if (queryValue.isNullOrBlank()) {
                 return@get call.respondText(
                     "Missing query value.",
                     status = HttpStatusCode.BadRequest
                 )
             }
             var limitValue = call.request.queryParameters["limit"]?.toIntOrNull() ?: return@get call.respondText(
                 "Missing limit value.",
                 status = HttpStatusCode.BadRequest
             )

             val res = repository.findUsers(queryValue, limitValue)
             when (res) {
                 is OutgoingMessage.Error ->
                     call.respondText(res.toJson(), status = HttpStatusCode.BadRequest)
                 is OutgoingMessage.SuccessUserResults ->
                     call.respondText(res.toJson(), status = HttpStatusCode.Accepted)
                 else -> call.respond(HttpStatusCode.NotImplemented)
             }
         }

        post {
            /* Return BadRequest request body is malformed. */
            val user = call.receiveNullable<NewUser>() ?: kotlin.run {
                call.respondText(ErrorTypes.REQUEST_BODY_MISSING.errorDetail, status = HttpStatusCode.BadRequest)
                return@post
            }
            /*
             Return Error message if error raised during new user insert.
             Return Accepted status is user added.
            */
            var res = repository.addUser(user)
            when (res) {
                is OutgoingMessage.ValidationError ->
                    call.respondText(res.toJson(), contentType = ContentType.Application.Json, status = HttpStatusCode.UnprocessableEntity)
                is OutgoingMessage.Error ->
                    call.respondText(res.toJson(), contentType = ContentType.Application.Json, status = HttpStatusCode.BadRequest)
                is OutgoingMessage.Success ->
                    call.respond(HttpStatusCode.Accepted)
                else -> call.respond(HttpStatusCode.NotImplemented)
            }
        }
//        delete("{id?}") {
//            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
//            if (userStorage.removeIf {it.id == id }) {
//                call.respondText("User successfully removed.", status = HttpStatusCode.Accepted)
//            } else {
//                call.respondText("Not found.", status = HttpStatusCode.NotFound)
//            }
//        }
    }

}
