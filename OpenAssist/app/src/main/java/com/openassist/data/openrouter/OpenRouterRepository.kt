package com.openassist.data.openrouter

import com.openassist.ui.chat.ChatMessage
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType

class OpenRouterRepository(
    private val api: OpenRouterApi = Retrofit.Builder()
        .baseUrl("https://openrouter.ai/api/v1/")
        .client(OkHttpClient.Builder().build())
        .addConverterFactory(Json { ignoreUnknownKeys = true }.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(OpenRouterApi::class.java),
) {
    suspend fun sendMessage(apiKey: String, model: String, history: List<ChatMessage>): String {
        require(apiKey.isNotBlank()) { "Add your OpenRouter API key in Settings first." }
        val response = api.chat(
            authorization = "Bearer $apiKey",
            request = ChatRequest(model, history.map { OpenRouterMessage(it.role.apiRole, it.content) }),
        )
        return response.choices.firstOrNull()?.message?.content.orEmpty()
            .ifBlank { "OpenRouter returned an empty response." }
    }

    suspend fun availableModels(): List<ModelInfo> = api.models().data
}

private val String.apiRole: String
    get() = when (this) {
        "assistant" -> "assistant"
        "system" -> "system"
        else -> "user"
    }
