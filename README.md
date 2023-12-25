##How to Use
Make sure [Ollama](https://ollama.ai/) is Installed and Running
´´´
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
´´´
