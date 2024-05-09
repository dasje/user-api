package io.memorix.messages

import kotlinx.serialization.Serializable

/* Request body schema for /users@post. */
@Serializable
data class NewUser(
    val name: String,
    val email: String,
    val password: String,
)

/* User query schema. */
@Serializable
data class UserResponse(
    val email: String,
    val name: String,
)

/* Response body schema for queries of existing users. */
@Serializable
data class QueryUsersResponse(
    val users: List<UserResponse>,
    val total: Int,
)
