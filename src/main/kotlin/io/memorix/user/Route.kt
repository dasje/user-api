package io.memorix.user

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.memorix.Routes
import org.koin.ktor.ext.inject

fun Route.user() {
    val repository: UserRepository by inject()

    route(Routes.USERS_ROUTE) {
//         get("/{id}") {
//             val id = call.parameters["id"] ?: return@get call.respondText(
//                 "Missing id",
//                 status = HttpStatusCode.BadRequest
//             )
//             val user = userStorage.find { it.id == id } ?: return@get call.respondText(
//                 "No customer with id $id",
//                 status = HttpStatusCode.NotFound
//             )
//             print("This is the id ${id}")
//             call.respond(user)
//         }
        post {
            val user = call.receive<NewUser>()
            var res = repository.addUser(user)
            println(res)
            call.respondText("User stored correctly.", status = HttpStatusCode.Created)
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
