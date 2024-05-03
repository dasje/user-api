package io.memorix

import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.memorix.plugins.*
import io.memorix.user.userDi
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.environmentProperties

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    startKoin()
    configureHTTP()
    configureSerialization()
    configureRouting()
}

fun startKoin(): Koin = startKoin {
    properties(
        dotenv {
            ignoreIfMalformed = true
            ignoreIfMissing = true
        }
            .entries()
            .associate {
                it.key to it.value
            }
    )

    environmentProperties()

    modules(
        applicationDi,
        userDi
    )
}.koin
