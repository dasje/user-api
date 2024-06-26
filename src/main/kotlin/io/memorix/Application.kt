package io.memorix

import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.memorix.modules.databaseDi
import io.memorix.plugins.*
import io.memorix.modules.userDi
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.logger.PrintLogger
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
    logger(PrintLogger(Level.INFO))

    properties(
        dotenv {
            ignoreIfMalformed = true
            ignoreIfMissing = false
        }
            .entries()
            .associate {
                it.key to it.value
            }
    )

    environmentProperties()

    modules(
        databaseDi,
        userDi
    )

    createEagerInstances()

}.koin
