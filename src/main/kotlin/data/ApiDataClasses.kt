package de.jackBeBack.data

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class CompletionRequest(val model: String, val prompt: String, val system: String)

@Serializable
data class GenerateResponse(
    val model: String,
    val created_at: String,
    val response: String,
    val done: Boolean,
    val context: String? = null
)

@Serializable
data class ChatResponse(
    val model: String,
    @Serializable(with = LocalDateTimeSerializer::class) val created_at: LocalDateTime,
    val message: Message,
    val done: Boolean
)

@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<Message>
)

@Serializable
data class Message(
    val role: Role,
    val content: String,
    val images: List<String>? = null
)

@Serializable
enum class Role {
    @SerialName("user") USER,
    @SerialName("system") SYSTEM,
    @SerialName("assistant") ASSISTANT
}


