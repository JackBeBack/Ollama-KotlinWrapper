package de.jackBeBack.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


val json = Json { ignoreUnknownKeys = true }
@OptIn(ExperimentalSerializationApi::class)
fun String.toGenerateResponse(): GenerateResponse? {
    return try {
        json.decodeFromString(this)
    } catch (e: Exception) {
        null
    }
}


@OptIn(ExperimentalSerializationApi::class)
fun String.toChatResponse(): ChatResponse? {
    return try {
        json.decodeFromString(this)
    } catch (e: Exception) {
        null
    }
}