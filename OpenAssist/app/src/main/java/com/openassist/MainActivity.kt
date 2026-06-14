package com.openassist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.openassist.data.local.SecureStorage
import com.openassist.ui.chat.ChatScreen
import com.openassist.ui.onboarding.OnboardingScreen
import com.openassist.viewmodel.ChatViewModel
import com.openassist.viewmodel.SettingsViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val storage = SecureStorage(this)
        setContent {
            MaterialTheme {
                val settings: SettingsViewModel = viewModel(factory = settingsFactory(storage))
                val apiKey by settings.apiKey.collectAsState()
                if (apiKey.isBlank()) {
                    OnboardingScreen(onSaveApiKey = settings::saveApiKey)
                } else {
                    val chat: ChatViewModel = viewModel(factory = chatFactory(storage))
                    ChatScreen(chatViewModel = chat, settingsViewModel = settings)
                }
            }
        }
    }
}

private fun settingsFactory(storage: SecureStorage) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = SettingsViewModel(storage) as T
}

private fun chatFactory(storage: SecureStorage) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ChatViewModel(storage) as T
}
