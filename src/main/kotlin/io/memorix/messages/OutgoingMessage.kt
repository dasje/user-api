package io.memorix.messages

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

sealed class OutgoingMessage<out T> {
    @Serializable
    data class Success<out T>(val data: T) : OutgoingMessage<T>()
    @Serializable
    data class Error(val error: String) : OutgoingMessage<Nothing>() {
        fun toJson(msg: Error = this): String = Json.encodeToString(msg)
    }

}