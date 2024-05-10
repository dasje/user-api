package io.memorix

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.memorix.messages.NewUser
import io.memorix.plugins.*
import io.memorix.user.UserRepository
import org.junit.After
import org.junit.Before
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import kotlin.test.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import io.memorix.database.DBConnector
import io.memorix.database.DBConnectorFacade
import io.memorix.user.UserFacade
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Rule
//import org.junit.jupiter.api.AfterAll
//import org.junit.jupiter.api.BeforeAll
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.TestInstance
//import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.environmentProperties
import org.koin.test.check.checkModules
//import org.koin.test.junit5.KoinTestExtension
import org.testcontainers.containers.PostgreSQLContainer

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApplicationTest(): KoinTest {
//    private val dbConnector by inject<DBConnectorFacade>()
//    private val userRepo by inject<UserFacade>()
    companion object {
        var postgres = PostgreSQLContainer("postgres:16.2-alpine")
        private lateinit var dbConfig: Map<String, String>
        @JvmStatic
        @BeforeClass
        fun before(): Unit {
            postgres.start()
            dbConfig = mapOf(
                "DB_HOST" to postgres.host.toString().trim(),
                "DB_NAME" to postgres.databaseName.toString().trim(),
                "DB_PASS" to postgres.password.toString().trim(),
                "DB_PORT" to postgres.firstMappedPort.toString().trim(),
                "DB_USER" to postgres.username.toString().trim()
            )
            println("DB CONFIG")
            println(dbConfig)
        }

        @JvmStatic
        @AfterClass
        fun after(): Unit {
            postgres.close()
        }
    }
    var companion = Companion



    @get:Rule
    val koinTestRule = KoinTestRule.create {
        properties(
            dbConfig
        )
        environmentProperties()
        modules(
            module {
                single<DBConnectorFacade> { DBConnector(
                    getProperty("DB_HOST"),
                    getProperty("DB_PORT"),
                    getProperty("DB_NAME"),
                    getProperty("DB_USER"),
                    getProperty("DB_USER"))
                }
                single { UserRepository(get()) }
            }
        )
    }

    @Test
    fun testPostUser() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        environment {
//            config = config.mergeWith(
//                MapApplicationConfig(
//                    "DB_HOST" to companion.dbConfig.get("DB_HOST"),
//                    "DB_PORT" to companion.dbConfig.get("DB_PORT")
//                )
//            )
        }
        application {
            configureRouting()
            configureSerialization()
        }
        println("DB CONFIG2")
        println(dbConfig)
//        println(configLoaders)
//        startKoin()
//        println(koinTestRule.koin.checkModules())

        val response = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(NewUser(name = "JSally", email = "teat@users.com", password = "any-random-string"))
        }
        println("RES")
        println(response.bodyAsText())
        assertEquals(HttpStatusCode.Accepted, response.status)
    }


}
