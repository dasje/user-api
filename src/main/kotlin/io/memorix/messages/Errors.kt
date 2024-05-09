package io.memorix.messages

import kotlinx.serialization.Serializable

@Serializable
data class Errors(
    val error: String
)

enum class ErrorTypes(val errorDetail: String) {
    UNPARSABLE_MESSAGE("Invalid message structure.")
}