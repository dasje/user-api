package io.memorix

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.memorix.database.DBConnectorFacade
import io.memorix.database.tables.Users
import io.memorix.messages.NewUser
import io.memorix.plugins.*
import io.memorix.user.UserRepository
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.After
import org.junit.Before
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import kotlin.test.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.memorix.user.UserFacade
import kotlinx.serialization.json.Json
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayNameGenerator.Simple
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.test.get
import org.koin.test.junit5.KoinTestExtension

object SchemaDefinition {
    fun createSchema() {
        transaction {
            SchemaUtils.create(Users)
        }
    }
}

class DatabaseFactoryForUnitTests(
    val dbHost: String,
    val dbPort: String,
    val dbName: String,
    val dbUser: String,
    val dbPass: String,
) : DBConnectorFacade {
    lateinit var source: HikariDataSource
    fun connect() {
        Database.connect(hikari())
        SchemaDefinition.createSchema()
    }
    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.h2.Driver"
        config.jdbcUrl = "jdbc:h2:~/test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
        config.maximumPoolSize = 2
        config.isAutoCommit = true
        config.validate()
        source = HikariDataSource(config)
        return source
    }

    fun close() {
        source.close()
    }
    override suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}

class ApplicationTest: KoinTest {
    private lateinit var databaseFactory: DatabaseFactoryForUnitTests

    private val userRepo: UserRepository by inject<UserRepository>()

//    @RegisterExtension
//    @JvmField
//    val koinTestExtension = KoinTestExtension.create {
//        modules(
//            module {
//                single { UserRepository(get()) }
//                single { databaseFactory }
//            })
//    }

    @Before
    fun setup() {
        databaseFactory = DatabaseFactoryForUnitTests("","","","","")
        databaseFactory.connect()
    }

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(io.memorix.modules.userDi)
    }

    @After
    fun tearDown() {
        databaseFactory.close()
    }

    @Test
    fun testPostUser() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        environment {
            config = MapApplicationConfig(listOf(
                Pair(first = "DB_HOST", second = ""),
                Pair(first = "DB_NAME", second = ""),
                Pair(first = "DB_PASS", second = ""),
                Pair(first = "DB_PORT", second = ""),
                Pair(first = "DB_USER", second = "")
            ))
        }
        application {
            configureRouting()
            configureSerialization()
        }


        val response = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(NewUser(name = "John Doe", email = "test@users.com", password = "any-random-string"))
        }

        assertEquals(HttpStatusCode.Accepted, response.status)
    }
}
