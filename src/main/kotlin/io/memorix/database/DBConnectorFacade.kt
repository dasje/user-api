package io.memorix.database

interface DBConnectorFacade {
    suspend fun <T> dbQuery(block: suspend () -> T): T
}