package io.memorix

import io.memorix.database.DBConnector
import io.memorix.database.DBConnectorFacade
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.withOptions
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import java.lang.System.getProperty

val applicationDi = module {
    val params = parametersOf(
        getProperty("DB_HOST"),
        getProperty("DB_PORT"),
        getProperty("DB_NAME"),
        getProperty("DB_USER"),
        getProperty("DB_PASS"),
        )
    println(params)
    single<DBConnectorFacade> {
        DBConnector(
            dbHost = params.get<String>(),
            dbPort = params.get<String>(),
            dbName = params.get<String>(),
            dbUser = params.get<String>(),
            dbPass = params.get<String>()
        )
    } withOptions {
        createdAtStart()
    }
}
