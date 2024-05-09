package io.memorix.messages

import kotlinx.serialization.Serializable

@Serializable
data class Errors(
    val error: String
)

enum class ErrorTypes(val errorDetail: String) {
    UNPARSEABLE_MESSAGE("Invalid message structure.")
}