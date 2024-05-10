package io.memorix

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.memorix.messages.NewUser
import io.memorix.plugins.*
import io.memorix.user.UserRepository
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import kotlin.test.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.memorix.database.DBConnector
import io.memorix.database.DBConnectorFacade
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Rule
import org.koin.dsl.module
import org.koin.environmentProperties
import org.testcontainers.containers.PostgreSQLContainer

class ApplicationTest(): KoinTest {
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
        environment { }
        application {
            configureRouting()
            configureSerialization()
        }

        val response = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(NewUser(name = "JSally", email = "teat@users.com", password = "any-random-string"))
        }
        assertEquals(HttpStatusCode.Accepted, response.status)
    }


}
