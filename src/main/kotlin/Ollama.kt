package de.jackBeBack

import de.jackBeBack.data.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Ollama(val host: String = "localhost", val port: Int = 11434, val model: String = "llama2") {

    /**
     * State of the Llama Server.
     */
    private val _currentState = MutableStateFlow<LLMSTATE>(LLMSTATE.WAITING)
    val currentState: StateFlow<LLMSTATE> = _currentState

    /**
     * Ktor Client with Timeout will be used by all Requests
     */
    private val client = HttpClient(CIO) {
        engine {
            // Configure timeouts
            requestTimeout = 300000 // 300 seconds
        }
    }


    /**
    Send the prompt to the server and return a flow of responses
    Flow will be empty until the server responds
    Flow will be closed when the server closes the connection
    Flow will be cancelled when the coroutine scope is cancelled
    @param prompt: The prompt to send to the server
    @param onFinish: A callback that will be called when the server closes the connection with the whole generated text
    @return A Flow of generated Tokens from the server
     */
    @OptIn(InternalAPI::class, ExperimentalSerializationApi::class)
    suspend fun generate(prompt: String, onFinish: (String) -> Unit = {}): Flow<String> {
        if (_currentState.value == LLMSTATE.RUNNING) {
            throw Exception("Already running")
        }
        val response: HttpResponse = client.post("http://$host:$port/api/generate") {
            contentType(ContentType.Application.Json)
            setBody(
                json.encodeToString(
                    CompletionRequest(
                        model,
                        prompt,
                        system = ""
                    )
                )
            )
        }
        return flow {
            var generatedText = ""
            val channel: ByteReadChannel = response.content
            try {
                while (true) {
                    if (channel.availableForRead > 0) {
                        val response = channel.readUTF8Line()?.toGenerateResponse()
                        if (response != null) {
                            generatedText += response.response
                            emit(response.response)
                        }
                    }
                    if (channel.isClosedForRead) break
                    delay(50) // A small delay to prevent tight looping
                }
                onFinish(generatedText)
            } catch (_: Exception) {
                // Handle specific exceptions here
            }
        }
    }

    /**
     * @param messages: history of messages latest message should be from the user
     * @param onFinish: Callback when Generation is finished. Will be called with the new messages history where the last entry is the most recent response form the server
     * @return A Flow of generated Tokens from the server
     */
    @OptIn(InternalAPI::class)
    suspend fun chat(messages: List<Message>, onFinish: (List<Message>) -> Unit = {}): Flow<String> {
        if (_currentState.value == LLMSTATE.RUNNING) {
            throw Exception("Already running")
        }
        val response: HttpResponse = client.post("http://$host:$port/api/chat") {
            contentType(ContentType.Application.Json)
            setBody(
                json.encodeToString(
                    ChatRequest(
                        model,
                        messages
                    )
                )
            )
        }

        return flow {
            var generatedText = ""
            val channel: ByteReadChannel = response.content
            try {
                while (true) {
                    if (channel.availableForRead > 0) {
                        val chatResponse = channel.readUTF8Line()?.toChatResponse()
                        if (chatResponse != null) {
                            generatedText += chatResponse.message.content
                            emit(chatResponse.message.content)
                        }
                    }
                    if (channel.isClosedForRead) break
                    delay(50) // A small delay to prevent tight looping
                }
                val newMessages = messages.toMutableList()
                newMessages.add(Message(Role.SYSTEM, generatedText))
                onFinish(newMessages)
            } catch (_: Exception) {
                // Handle specific exceptions here
            }
        }
    }

    /**
     * @param prompt: The prompt to generate the embedding for
     * @return The embedding of the prompt
     */
    @OptIn(InternalAPI::class)
    suspend fun embedding(prompt: String): Embedding?{
        if (_currentState.value == LLMSTATE.RUNNING) {
            throw Exception("Already running")
        }
        val response: HttpResponse = client.post("http://$host:$port/api/embeddings") {
            contentType(ContentType.Application.Json)
            setBody(
                json.encodeToString(
                    EmbeddingRequest(
                        model,
                        prompt
                    )
                )
            )
        }

        val channel: ByteReadChannel = response.content
        var ret = Embedding(listOf())
        try {
            while (true) {
                if (channel.availableForRead > 0) {
                    val embedding = channel.readUTF8Line()?.toEmbedding()
                    if (embedding != null) {
                        ret = embedding
                    }
                }
                if (channel.isClosedForRead) break
                delay(50) // A small delay to prevent tight looping
            }
        } catch (_: Exception) {
            // Handle specific exceptions here
        }
        return ret
    }

    /**
     * @return The list of available models
     */
    @OptIn(InternalAPI::class)
    suspend fun listModels(): Models{
        if (_currentState.value == LLMSTATE.RUNNING) {
            throw Exception("Already running")
        }
        val response: HttpResponse = client.get("http://$host:$port/api/tags") {
            contentType(ContentType.Application.Json)
        }

        val channel: ByteReadChannel = response.content
        var ret = Models(listOf())
        try {
            while (true) {
                if (channel.availableForRead > 0) {
                    val models = channel.readUTF8Line()?.toModels()
                    if (models != null) {
                        ret = models
                    }
                }
                if (channel.isClosedForRead) break
                delay(50) // A small delay to prevent tight looping
            }
        } catch (_: Exception) {
            // Handle specific exceptions here
        }
        return ret
    }
}


/**
 * State Class for the Llama Server
 */
enum class LLMSTATE {
    WAITING,
    RUNNING
}