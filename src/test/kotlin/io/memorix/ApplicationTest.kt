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
import io.ktor.client.statement.*
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

    @Test
    fun testPostDuplicateUser() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        application {
            configureRouting()
            configureSerialization()
        }
        client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(NewUser(name = "JSally", email = "test@users.com", password = "any-random-string"))
        }
        val response = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(NewUser(name = "JSally", email = "test@users.com", password = "any-random-string"))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("{\"error\":\"Duplicate e-mail: test@users.com\"}", response.bodyAsText())
    }

    @Test
    fun testGetUsers() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        application {
            configureRouting()
            configureSerialization()
        }
        client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(NewUser(name = "John Bercow", email = "jb@users.com", password = "any-random-string"))
        }
        client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(NewUser(name = "Johnny Rotten", email = "jr@users.com", password = "any-random-string"))
        }
        client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(NewUser(name = "Jon Boy", email = "jm@users.com", password = "any-random-string"))
        }
        val response = client.get("/users") {
            contentType(ContentType.Application.Json)
            parameter("query","John")
            parameter("limit","3")
        }

        assertEquals(HttpStatusCode.Accepted, response.status)
        assertEquals("{\"users\":[{\"email\":\"jb@users.com\",\"name\":\"John Bercow\"},{\"email\":\"jr@users.com\",\"name\":\"Johnny Rotten\"}],\"total\":2}", response.bodyAsText())
    }

    @Test
    fun testGetMissingParameterValues() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        application {
            configureRouting()
            configureSerialization()
        }
        val responseLimit = client.get("/users") {
            contentType(ContentType.Application.Json)
            parameter("query","John")
        }

        assertEquals(HttpStatusCode.BadRequest, responseLimit.status)
        assertEquals("Missing limit value.", responseLimit.bodyAsText())

        val responseQuery = client.get("/users") {
            contentType(ContentType.Application.Json)
            parameter("limit","3")
        }

        assertEquals(HttpStatusCode.BadRequest, responseQuery.status)
        assertEquals("Missing query value.", responseQuery.bodyAsText())
    }

}
