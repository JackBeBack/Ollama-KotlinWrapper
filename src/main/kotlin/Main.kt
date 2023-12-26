package de.jackBeBack

import de.jackBeBack.data.Message
import de.jackBeBack.data.Role
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val ollama = Ollama()

        // A mutable list of Message objects is created to store the chat history.
        var history = mutableListOf<Message>()

        // An infinite loop is started to continuously accept user input.
        while (true) {
            println("User:")

            // The user's input is read from the console.
            val message = readLine()

            // If the user's input is null or empty, the loop is broken and the program ends.
            if (message.isNullOrEmpty()) break

            println("Assistant:")

            // The user's message is added to the chat history.
            history.add(Message(Role.USER, message))

            // The chat method of the Ollama object is called with the chat history and a callback
            // function.
            // The callback function updates the chat history with the new history returned by the
            // chat method.
            ollama.chat(
                history,
                onFinish = {
                    history = it.toMutableList()
                    println()
                }
            )
                .collect {
                    // The messages from the chat are collected and printed to the console.
                    print(it)
                }
        }
    }
}
