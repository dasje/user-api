package io.memorix.messages

import kotlinx.serialization.Serializable

@Serializable
data class ErrorDetails(
    val error: String
)

enum class ErrorTypes(val errorDetail: String) {
    UNPARSEABLE_MESSAGE("Invalid message structure.")
}