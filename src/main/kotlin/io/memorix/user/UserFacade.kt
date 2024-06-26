package io.memorix.user

import io.memorix.messages.NewUser
import io.memorix.messages.OutgoingMessage
import io.memorix.messages.QueryUsersResponse
import java.util.UUID

interface UserFacade {
    suspend fun findUsers(nameFragment: String, limitValue: Int): OutgoingMessage<QueryUsersResponse>?
    suspend fun addUser(newUser: NewUser): OutgoingMessage<Boolean>?
    suspend fun removeUser(id: UUID): Boolean
}