package com.openassist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openassist.data.local.SecureStorage
import com.openassist.data.openrouter.OpenRouterRepository
import com.openassist.ui.chat.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
)

class ChatViewModel(
    private val storage: SecureStorage,
    private val repository: OpenRouterRepository = OpenRouterRepository(),
) : ViewModel() {
    private val _state = MutableStateFlow(ChatUiState())
    val state: StateFlow<ChatUiState> = _state.asStateFlow()

    fun send(content: String) {
        if (content.isBlank()) return
        val nextMessages = _state.value.messages + ChatMessage(role = "user", content = content.trim())
        _state.value = _state.value.copy(messages = nextMessages, loading = true, error = null)
        viewModelScope.launch {
            runCatching { repository.sendMessage(storage.apiKey.value, storage.model.value, nextMessages) }
                .onSuccess { reply ->
                    _state.value = _state.value.copy(
                        messages = nextMessages + ChatMessage(role = "assistant", content = reply),
                        loading = false,
                    )
                }
                .onFailure { error -> _state.value = _state.value.copy(loading = false, error = error.message) }
        }
    }
}
