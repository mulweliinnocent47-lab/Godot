package com.openassist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openassist.data.local.SecureStorage
import com.openassist.data.openrouter.OpenRouterRepository
import com.openassist.data.openrouter.argumentsAsMap
import com.openassist.data.openrouter.toOpenRouterMessage
import com.openassist.data.openrouter.toOpenRouterMessages
import com.openassist.tools.ToolEngine
import com.openassist.tools.ToolRequest
import com.openassist.ui.chat.ChatMessage
import kotlinx.serialization.json.Json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val pendingTool: PendingToolConfirmation? = null,
)

data class PendingToolConfirmation(
    val name: String,
    val arguments: Map<String, String>,
)

private data class PendingToolCall(
    val request: ToolRequest,
    val toolCallId: String,
)

private data class ToolLoopResult(
    val reply: String,
    val pendingTool: PendingToolCall? = null,
)

class ChatViewModel(
    private val storage: SecureStorage,
    private val repository: OpenRouterRepository = OpenRouterRepository(),
    private val toolEngine: ToolEngine,
) : ViewModel() {
    private val _state = MutableStateFlow(ChatUiState())
    val state: StateFlow<ChatUiState> = _state.asStateFlow()
    private val json = Json { ignoreUnknownKeys = true }
    private var pendingOpenRouterMessages = emptyList<com.openassist.data.openrouter.OpenRouterMessage>()
    private var pendingToolCall: PendingToolCall? = null

    fun send(content: String) {
        if (content.isBlank()) return
        val nextMessages = _state.value.messages + ChatMessage(role = "user", content = content.trim())
        _state.value = _state.value.copy(messages = nextMessages, loading = true, error = null, pendingTool = null)
        viewModelScope.launch {
            runCatching { runToolCallingLoop(nextMessages) }
                .onSuccess { result ->
                    _state.value = _state.value.copy(
                        messages = nextMessages + ChatMessage(role = "assistant", content = result.reply),
                        loading = false,
                        pendingTool = result.pendingTool?.let {
                            PendingToolConfirmation(it.request.name, it.request.arguments)
                        },
                    )
                }
                .onFailure { error -> _state.value = _state.value.copy(loading = false, error = error.message) }
        }
    }

    fun respondToPendingTool(allow: Boolean) {
        val pending = _state.value.pendingTool ?: return
        val messages = _state.value.messages
        _state.value = _state.value.copy(loading = true, error = null, pendingTool = null)
        viewModelScope.launch {
            runCatching {
                if (!allow) {
                    pendingOpenRouterMessages = emptyList()
                    pendingToolCall = null
                    "Denied ${pending.name}."
                } else {
                    val toolCallId = pendingToolCall?.toolCallId ?: return@runCatching "No pending tool call found."
                    val result = toolEngine.execute(ToolRequest(pending.name, pending.arguments), confirmed = true)
                    val continuedMessages = pendingOpenRouterMessages.toMutableList().apply {
                        add(result.toOpenRouterMessage(toolCallId))
                    }
                    pendingOpenRouterMessages = emptyList()
                    pendingToolCall = null
                    continueToolCallingLoop(continuedMessages).reply
                }
            }.onSuccess { reply ->
                _state.value = _state.value.copy(
                    messages = messages + ChatMessage(role = "assistant", content = reply),
                    loading = false,
                )
            }.onFailure { error -> _state.value = _state.value.copy(loading = false, error = error.message) }
        }
    }

    private suspend fun runToolCallingLoop(messages: List<ChatMessage>): ToolLoopResult {
        return continueToolCallingLoop(messages.toOpenRouterMessages().toMutableList())
    }

    private suspend fun continueToolCallingLoop(openRouterMessages: MutableList<com.openassist.data.openrouter.OpenRouterMessage>): ToolLoopResult {
        repeat(MAX_TOOL_ROUNDS) {
            val choice = repository.createChatCompletion(
                apiKey = storage.apiKey.value,
                model = storage.model.value,
                messages = openRouterMessages,
                tools = toolEngine.toolDefinitions(),
            )
            val assistantMessage = choice.message
            val toolCalls = assistantMessage.toolCalls.orEmpty()
            if (toolCalls.isEmpty()) {
                return ToolLoopResult(assistantMessage.content.orEmpty().ifBlank { "OpenRouter returned an empty response." })
            }
            openRouterMessages += assistantMessage
            toolCalls.forEach { toolCall ->
                val request = ToolRequest(
                    name = toolCall.function.name,
                    arguments = toolCall.argumentsAsMap(json),
                )
                val result = toolEngine.execute(request, confirmed = false)
                if (result.requiresConfirmation) {
                    pendingOpenRouterMessages = openRouterMessages
                    pendingToolCall = PendingToolCall(request, toolCall.id)
                    return ToolLoopResult(
                        reply = "I need your confirmation before running ${request.name} with: ${request.arguments}",
                        pendingTool = pendingToolCall,
                    )
                }
                openRouterMessages += result.toOpenRouterMessage(toolCall.id)
            }
        }
        return ToolLoopResult("The assistant requested too many tool calls. Please try a more specific request.")
    }

    private companion object {
        const val MAX_TOOL_ROUNDS = 5
    }
}
