package io.memorix.messages

import kotlinx.serialization.Serializable

@Serializable
data class SuccessDetails(
    val success: String
)

enum class SuccessTypes(val successDetail: String) {
    SUCCESS("Success."),
    USER_ADDED("User added.")
}