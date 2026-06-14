package com.openassist.viewmodel

import androidx.lifecycle.ViewModel
import com.openassist.data.local.SecureStorage

class SettingsViewModel(private val storage: SecureStorage) : ViewModel() {
    val apiKey = storage.apiKey
    val model = storage.model

    fun saveApiKey(value: String) = storage.saveApiKey(value)
    fun clearApiKey() = storage.clearApiKey()
    fun saveModel(value: String) = storage.saveModel(value)
    fun reset() = storage.reset()
}
