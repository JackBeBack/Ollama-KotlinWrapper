## How to Use
Make sure [Ollama](https://ollama.ai/) is Installed and Running
```kotlin
fun main() {
    runBlocking {
        //select a model
        val ollama = Ollama(model = "wizard-vicuna")
        ollama.ask("What is the weather in Berlin?").collect{
            print(it)
        }
    }
}
```
