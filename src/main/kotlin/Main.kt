package de.jackBeBack

import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val ollama = Ollama(model = "wizard-vicuna")
        ollama.ask("What is the weather in Berlin?"){
            println(it)
        }.collect{
            print(it)
        }
    }
}