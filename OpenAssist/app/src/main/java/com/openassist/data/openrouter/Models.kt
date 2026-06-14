package com.openassist.data.openrouter

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<OpenRouterMessage>,
    val tools: List<ToolDefinition> = emptyList(),
)

@Serializable
data class OpenRouterMessage(
    val role: String,
    val content: String? = null,
    @SerialName("tool_call_id") val toolCallId: String? = null,
    @SerialName("tool_calls") val toolCalls: List<ToolCall>? = null,
)

@Serializable
data class ChatResponse(val choices: List<Choice> = emptyList())

@Serializable
data class Choice(val message: OpenRouterMessage)

@Serializable
data class ToolDefinition(
    val type: String = "function",
    val function: ToolFunction,
)

@Serializable
data class ToolFunction(
    val name: String,
    val description: String,
    val parameters: ToolParameters = ToolParameters(),
)

@Serializable
data class ToolParameters(
    val type: String = "object",
    val properties: Map<String, ToolParameterProperty> = emptyMap(),
    val required: List<String> = emptyList(),
)

@Serializable
data class ToolParameterProperty(
    val type: String,
    val description: String,
)

@Serializable
data class ToolCall(
    val id: String,
    val type: String = "function",
    val function: ToolCallFunction,
)

@Serializable
data class ToolCallFunction(
    val name: String,
    val arguments: String = "{}",
)

@Serializable
data class ModelsResponse(val data: List<ModelInfo> = emptyList())

@Serializable
data class ModelInfo(
    val id: String,
    val name: String? = null,
    @SerialName("context_length") val contextLength: Int? = null,
)
