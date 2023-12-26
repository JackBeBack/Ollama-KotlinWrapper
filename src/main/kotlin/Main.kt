package de.jackBeBack

import de.jackBeBack.data.ChatRequest
import de.jackBeBack.data.Message
import de.jackBeBack.data.Role
import kotlinx.coroutines.runBlocking
import java.sql.SQLOutput

fun main() {
    runBlocking {
        val ollama = Ollama()
        var history = mutableListOf<Message>()
        while (true){
            println("User:")
            val message = readLine()
            if (message.isNullOrEmpty()) break
            println("Assistant:")
            history.add(Message(Role.USER, message))
            ollama.chat(history, onFinish = {
                history = it.toMutableList()
                println()
            }).collect{
                print(it)
            }
        }
    }
}