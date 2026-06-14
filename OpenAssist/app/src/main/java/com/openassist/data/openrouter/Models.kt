package com.openassist.data.openrouter

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<OpenRouterMessage>,
)

@Serializable
data class OpenRouterMessage(
    val role: String,
    val content: String,
)

@Serializable
data class ChatResponse(val choices: List<Choice> = emptyList())

@Serializable
data class Choice(val message: OpenRouterMessage)

@Serializable
data class ModelsResponse(val data: List<ModelInfo> = emptyList())

@Serializable
data class ModelInfo(
    val id: String,
    val name: String? = null,
    @SerialName("context_length") val contextLength: Int? = null,
)
