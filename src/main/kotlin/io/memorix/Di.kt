package io.memorix

import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module

val applicationDi = module {
    single<Database> {
        Database.connect(
            url = "jdbc:postgresql://" + getProperty("DB_HOST") + ":" + getProperty("DB_PORT") + "/" + getProperty("DB_NAME") + "?sslmode=disable",
            user = getProperty("DB_USER"),
            password = getProperty("DB_PASS")
        )
    }
}
