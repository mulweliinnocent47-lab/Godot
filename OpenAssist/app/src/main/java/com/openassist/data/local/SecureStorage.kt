package com.openassist.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SecureStorage(context: Context) {
    private val prefs = EncryptedSharedPreferences.create(
        context,
        "openassist_secure_prefs",
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    private val _apiKey = MutableStateFlow(prefs.getString(KEY_API_KEY, "").orEmpty())
    private val _model = MutableStateFlow(prefs.getString(KEY_MODEL, DEFAULT_MODEL) ?: DEFAULT_MODEL)

    val apiKey: StateFlow<String> = _apiKey
    val model: StateFlow<String> = _model

    fun saveApiKey(value: String) {
        prefs.edit().putString(KEY_API_KEY, value.trim()).apply()
        _apiKey.value = value.trim()
    }

    fun clearApiKey() = saveApiKey("")

    fun saveModel(value: String) {
        prefs.edit().putString(KEY_MODEL, value.trim()).apply()
        _model.value = value.trim()
    }

    fun reset() {
        prefs.edit().clear().apply()
        _apiKey.value = ""
        _model.value = DEFAULT_MODEL
    }

    companion object {
        const val DEFAULT_MODEL = "openai/gpt-4o-mini"
        private const val KEY_API_KEY = "openrouter_api_key"
        private const val KEY_MODEL = "openrouter_model"
    }
}
