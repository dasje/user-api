package io.memorix

import io.memorix.database.DBConnector
import io.memorix.database.DBConnectorFacade
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.withOptions
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import io.ktor.server.routing.*
import java.lang.System.getProperty

val applicationDi = module {
    single<DBConnectorFacade> {
        DBConnector(
            dbHost = getProperty("DB_HOST"),
            dbName = getProperty("DB_NAME"),
            dbPass = getProperty("DB_PASS"),
            dbPort = getProperty("DB_PORT"),
            dbUser = getProperty("DB_USER")
        )
    } withOptions {
        createdAtStart()
    }
}
