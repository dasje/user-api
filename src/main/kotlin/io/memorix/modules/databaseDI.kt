package io.memorix.modules

import io.memorix.database.DBConnector
import io.memorix.database.DBConnectorFacade
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module

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
