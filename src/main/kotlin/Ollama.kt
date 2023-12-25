package de.jackBeBack

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Ollama(val host: String = "localhost", val port: Int = 11434, val model: String = "llama2") {

    private val client = HttpClient(CIO) {
        engine {
            // Configure timeouts
            requestTimeout = 3000000 // 3000 seconds
        }
    }

    private val json = Json { ignoreUnknownKeys = true }


    /*
    Send the prompt to the server and return a flow of responses
    The flow will be empty until the server responds
    The flow will be closed when the server closes the connection
    The flow will be cancelled when the coroutine scope is cancelled
    @param prompt: The prompt to send to the server
    @param onFinish: A callback that will be called when the server closes the connection with the whole generated text
     */
    @OptIn(InternalAPI::class, ExperimentalSerializationApi::class)
    suspend fun ask(prompt: String, onFinish: (String) -> Unit = {}): Flow<String> {
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
                        val response = channel.readUTF8Line()?.toOllamaResponse()
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

    @Serializable
    data class CompletionRequest(val model: String, val prompt: String, val system: String)

    @Serializable
    data class OllamaResponse(
        val model: String,
        val created_at: String,
        val response: String,
        val done: Boolean,
        val context: String? = null
    )

    @OptIn(ExperimentalSerializationApi::class)
    private fun String.toOllamaResponse(): OllamaResponse? {
        return try {
            json.decodeFromString(this)
        } catch (e: Exception) {
            null
        }
    }
}