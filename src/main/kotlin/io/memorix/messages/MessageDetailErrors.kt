package io.memorix.messages

import kotlinx.serialization.Serializable

@Serializable
data class ErrorDetails(
    val error: String
)

enum class ErrorTypes(val errorDetail: String) {
    UNPARSEABLE_MESSAGE("Invalid message structure."),
    REQUEST_BODY_MISSING("Request body is missing."),
    PWD_ERR_LEN("Password must have at least eight characters!"),
    PWD_ERR_LOWER("Password must have at least one lowercase letter!"),
    PWD_ERR_DIGIT("Password must contain at least one digit!"),
    PWD_ERR_UPPER("Password must have at least one uppercase letter!"),
    PWD_ERR_SPECIAL("Password must have at least one special character, such as: _%-=+#@"),
    EMAIL_ERR_FORMAT("Invalid email address provided.")
}