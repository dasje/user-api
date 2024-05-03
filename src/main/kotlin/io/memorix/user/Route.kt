package io.memorix.user

import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.user() {
    val repository: UserRepository by inject()

    // Add your routes here
}
