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
import io.ktor.serialization.kotlinx.json.*
import io.memorix.database.DBConnector
import org.junit.Rule
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.test.junit5.KoinTestExtension
import org.testcontainers.containers.PostgreSQLContainer


class ApplicationTest: KoinTest {
    var postgres = PostgreSQLContainer("postgres:16.2-alpine")

    private lateinit var dbConfig: List<Pair<String, String>>

    private val userRepo: UserRepository by inject<UserRepository>()

    @Before
    fun before() {
        postgres.start()
        dbConfig = listOf(
            Pair(first = "DB_HOST", second = postgres.getHost().toString().trim()),
            Pair(first = "DB_NAME", second = postgres.getDatabaseName().toString().trim()),
            Pair(first = "DB_PASS", second = postgres.getPassword().toString().trim()),
            Pair(first = "DB_PORT", second = postgres.getFirstMappedPort().toString().trim()),
            Pair(first = "DB_USER", second = postgres.getUsername().toString().trim())
        )
        println("DB CONFIG")
        println(dbConfig)
        println(dbConfig[0].second)
    }

    @After
    fun after() {
        postgres.close()
    }

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(
            module {
                single { DBConnector(
                    dbConfig[0].second,
                    dbConfig[3].second,
                    dbConfig[1].second,
                    dbConfig[4].second,
                    dbConfig[2].second
                ) }
                single { UserRepository(get()) }
            }
        )
    }

//    @get:Rule
//    val koinTestRule = KoinTestRule.create {
//        modules(
//            io.memorix.modules.userDi,
//            io.memorix.modules.databaseDi
//        )
//    }

    @Test
    fun testPostUser() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        environment {
            config = MapApplicationConfig(dbConfig)
        }
        application {
            configureRouting()
            configureSerialization()
        }
        println("DB CONFIG2")
        println(dbConfig)
        startKoin(
            
        )

        val response = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(NewUser(name = "John Doe", email = "test@users.com", password = "any-random-string"))
        }

        assertEquals(HttpStatusCode.Accepted, response.status)
    }
}
