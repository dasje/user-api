package io.memorix.user

import java.util.UUID

interface UserFacade {
    suspend fun findUsers(nameFragment: String): QueryUsersResponse?
    suspend fun addUser(newUser: NewUser): NewUser?
    suspend fun removeUser(id: UUID): Boolean
}