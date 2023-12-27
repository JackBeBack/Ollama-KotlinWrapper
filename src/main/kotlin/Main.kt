package de.jackBeBack

import de.jackBeBack.data.Message
import de.jackBeBack.data.Role
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val ollama = Ollama()
        val embedding = ollama.embedding("Here is an article about llamas...")
        println(embedding)
    }
}
