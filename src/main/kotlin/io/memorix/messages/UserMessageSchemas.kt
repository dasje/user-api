package io.memorix.messages

import kotlinx.serialization.Serializable

@Serializable
data class NewUser(
    val name: String,
    val email: String,
    val password: String,
)

@Serializable
data class UserResponse(
    val email: String,
    val name: String,
)

@Serializable
data class QueryUsersResponse(
    val users: List<UserResponse>,
    val total: Int,
)