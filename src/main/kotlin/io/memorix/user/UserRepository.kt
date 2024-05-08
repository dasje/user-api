package io.memorix.user

import io.memorix.database.DBConnectorFacade
//import io.memorix.database.DBConnector.dbQuery
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
//import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement
import java.util.*

class UserRepository(
    private val database: DBConnectorFacade
) : UserFacade {
    override suspend fun findUsers(nameFragment: String): QueryUsersResponse? {
        lateinit var selectedUsers: List<UserResponse>
        coroutineScope {
            launch {
                selectedUsers = database.dbQuery {
                    Users
                        .select { Users.userName like nameFragment }
                        .map(::resultRowsToFoundUsers)
                }
            }
        }
        return QueryUsersResponse(selectedUsers, selectedUsers.size)
    }

    private fun resultRowsToFoundUsers(resultRow: ResultRow) = UserResponse(
        email = resultRow[Users.userEmail],
        name = resultRow[Users.userEmail]
    )

    override suspend fun addUser(newUser: NewUser): NewUser? {
        var newId = UUID.randomUUID()
        var name = newUser.name
        var email = newUser.email
        // TODO: hash incoming password
        var hashedPwd = newUser.password
        var insertStatement: InsertStatement<Number> = database.dbQuery {
            Users.insert {
                it[Users.id] = newId
                it[Users.userName] = name
                it[Users.userEmail] = email
                it[Users.hashedPassword] = hashedPwd
            }
        }
        var user = insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToNewUser)
        return user
    }

    private fun resultRowToNewUser(resultRow: ResultRow) = NewUser(
        name = resultRow[Users.userName],
        email = resultRow[Users.userEmail],
        password = resultRow[Users.hashedPassword],
    )

    override suspend fun removeUser(id: UUID): Boolean = database.dbQuery {
        Users.deleteWhere() { Users.id eq id } > 0
    }
}
