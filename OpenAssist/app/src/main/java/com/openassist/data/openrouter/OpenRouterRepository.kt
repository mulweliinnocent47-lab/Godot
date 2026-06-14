package com.openassist.data.openrouter

import com.openassist.ui.chat.ChatMessage
import com.openassist.tools.ToolResult
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
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
        return createChatCompletion(apiKey, model, history.toOpenRouterMessages()).message.content.orEmpty()
            .ifBlank { "OpenRouter returned an empty response." }
    }

    suspend fun createChatCompletion(
        apiKey: String,
        model: String,
        messages: List<OpenRouterMessage>,
        tools: List<ToolDefinition> = emptyList(),
    ): Choice {
        require(apiKey.isNotBlank()) { "Add your OpenRouter API key in Settings first." }
        val response = api.chat(
            authorization = "Bearer $apiKey",
            request = ChatRequest(model = model, messages = messages, tools = tools),
        )
        return response.choices.firstOrNull() ?: Choice(
            OpenRouterMessage(role = "assistant", content = "OpenRouter returned an empty response."),
        )
    }

    suspend fun availableModels(): List<ModelInfo> = api.models().data
}

fun List<ChatMessage>.toOpenRouterMessages(): List<OpenRouterMessage> = map {
    OpenRouterMessage(role = it.role.apiRole, content = it.content)
}

fun ToolCall.argumentsAsMap(json: Json): Map<String, String> {
    val parsed = runCatching { json.parseToJsonElement(function.arguments) as? JsonObject }.getOrNull()
    return parsed.orEmpty().mapValues { (_, value) -> value.asString() }
}

fun ToolResult.toOpenRouterMessage(toolCallId: String): OpenRouterMessage = OpenRouterMessage(
    role = "tool",
    content = output,
    toolCallId = toolCallId,
)

private fun JsonElement.asString(): String = runCatching { jsonPrimitive.content }.getOrElse { toString() }

private val String.apiRole: String
    get() = when (this) {
        "assistant" -> "assistant"
        "system" -> "system"
        else -> "user"
    }
