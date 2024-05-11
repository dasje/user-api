package io.memorix.user

import io.memorix.authentication.PasswordAuthentication
import io.memorix.database.DBConnectorFacade
import io.memorix.database.tables.Users
import io.memorix.inputvalidation.UserInputValidation
import io.memorix.messages.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement
import java.util.*

class UserRepository(
    private val database: DBConnectorFacade
) : UserFacade {

    /* Parse Query result into UserResponse model. */
    private fun resultRowsToFoundUsers(resultRow: ResultRow) = UserResponse(
        email = resultRow[Users.userEmail],
        name = resultRow[Users.userName]
    )

    /* Parse Query result to NewUser response model. */
    private fun resultRowToNewUser(resultRow: ResultRow) = NewUser(
        name = resultRow[Users.userName],
        email = resultRow[Users.userEmail],
        password = resultRow[Users.hashedPassword],
    )

    /* Return True is user email already exsists. */
    suspend fun findUserEmail(email: String): Boolean = database.dbQuery {
        !Users.select { Users.userEmail eq email }.empty()
    }

    /* Return all users where provided name fragment matches name value in database. */
    override suspend fun findUsers(nameFragment: String, limitValue: Int): OutgoingMessage<QueryUsersResponse>? {
        try {
            var selectedUsers: List<UserResponse> = database.dbQuery {
                Users.selectAll().andWhere { Users.userName regexp "^${nameFragment}" }.limit(limitValue).map(::resultRowsToFoundUsers)
            }
            var userResponse = QueryUsersResponse(selectedUsers, selectedUsers.size)
            return OutgoingMessage.SuccessUserResults(userResponse)
        } catch (e: Exception) {
            return OutgoingMessage.Error(error = e.localizedMessage)
        }
    }

    /*
        Return OutgoingMessage.Error if user email already exists in database or password format does not validate.
        User password is hashed and new user is added to database.
        On success, return OutgoingMessage.Success.
     */
    override suspend fun addUser(newUser: NewUser): OutgoingMessage<Boolean>? {
        if (findUserEmail(newUser.email)) {
            return OutgoingMessage.Error(error = "Duplicate e-mail: ${newUser.email}")
        }

        val passwordVal = UserInputValidation.validatePassword(newUser.password)
        when (passwordVal) {
            is OutgoingMessage.ValidationError -> return passwordVal
            else -> {}
        }

        val emailVal = UserInputValidation.validateEmail(newUser.email)
        when (emailVal) {
            is OutgoingMessage.ValidationError -> return emailVal
            else -> {}
        }

        val newId = UUID.randomUUID()
        val name = newUser.name
        val email = newUser.email
        val hashedPwd = PasswordAuthentication.hashPassword(newUser.password)
        val insertStatement: InsertStatement<Number> = database.dbQuery {
            Users.insert {
                it[Users.id] = newId
                it[Users.userName] = name
                it[Users.userEmail] = email
                it[Users.hashedPassword] = hashedPwd
            }
        }
        var user = insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToNewUser)
        return OutgoingMessage.Success(true)
    }

    /* Remove user by row id. */
    override suspend fun removeUser(id: UUID): Boolean = database.dbQuery {
        Users.deleteWhere() { Users.id eq id } > 0
    }
}
