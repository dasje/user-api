package io.memorix.database

import io.memorix.user.Users
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class DBConnector(
    val dbHost: String,
    val dbPort: String,
    val dbName: String,
    val dbUser: String,
    val dbPass: String,
) : DBConnectorFacade {
    private fun connectToDB(): Database {
        println("DBConnector being inited")
        println("HERE $dbUser")
        val driver = "org.postgresql.Driver"
        val url = "jdbc:postgresql://$dbHost:$dbPort/$dbName?sslmode=disable"

        val db = Database.connect(url = url, user = dbUser, password = dbPass, driver = driver)

        transaction(db) {
            SchemaUtils.create(Users)
        }
        return db
    }

    val database = connectToDB()

    override suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

//    fun init() {
//        println("DBConnector being inited")
//        val driver = "org.postgresql.Driver"
//        val dbUser = System.getenv("DB_USER")
//        val dbPwd = System.getenv("DB_PASS")
//        val dbHost = System.getenv("DB_HOST")
//        val dbPort = System.getenv("DB_PORT")
//        val dbName = System.getenv("DB_NAME")
//        println("HERE ${dbUser}")
//
//        val url = "jdbc:postgresql://$dbHost:$dbPort/$dbName?sslmode=disable"
//
//        val db = Database.connect(url = url, user = dbUser, password = dbPwd, driver = driver)
//
//        transaction(db) {
//            SchemaUtils.create(Users)
//        }
//    }
//    override suspend fun <T> dbQuery(block: suspend () -> T): T =
//        newSuspendedTransaction(Dispatchers.IO) { block() }
}