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
data class Embedding(val embedding: List<Double>)

@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<Message>
)

@Serializable
data class EmbeddingRequest(
    val model: String,
    val prompt: String
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

@Serializable
data class Models(
    val models: List<Model>
)

@Serializable
data class Model(
    val name: String,
    val modified_at: String,
    val size: Long,
    val digest: String,
    val details: ModelDetail
)

@Serializable
data class ModelDetail(
    val format: String,
    val family: String,
    val families: String?,
    val parameter_size: String,
    val quantization_level: String
)



