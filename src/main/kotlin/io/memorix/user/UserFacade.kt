package io.memorix.user

import io.memorix.messages.NewUser
import io.memorix.messages.OutgoingMessage
import io.memorix.messages.QueryUsersResponse
import io.memorix.messages.ReturnNewUser
import java.util.UUID

interface UserFacade {
    suspend fun findUsers(nameFragment: String): QueryUsersResponse?
    suspend fun addUser(newUser: NewUser): OutgoingMessage<Boolean>?
    suspend fun removeUser(id: UUID): Boolean
}