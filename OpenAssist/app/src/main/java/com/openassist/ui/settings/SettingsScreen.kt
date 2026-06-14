package com.openassist.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.openassist.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onBack: () -> Unit) {
    val savedKey by viewModel.apiKey.collectAsState()
    val savedModel by viewModel.model.collectAsState()
    var apiKey by remember(savedKey) { mutableStateOf(savedKey) }
    var model by remember(savedModel) { mutableStateOf(savedModel) }

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(apiKey, { apiKey = it }, Modifier.fillMaxWidth(), label = { Text("OpenRouter API key") }, visualTransformation = PasswordVisualTransformation())
        OutlinedTextField(model, { model = it }, Modifier.fillMaxWidth(), label = { Text("Model ID") })
        Text("Current model: $savedModel")
        Row {
            Button(onClick = { viewModel.saveApiKey(apiKey); viewModel.saveModel(model); onBack() }) { Text("Save") }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = viewModel::clearApiKey) { Text("Clear key") }
        }
        TextButton(onClick = viewModel::reset) { Text("Reset app configuration") }
        TextButton(onClick = onBack) { Text("Back") }
    }
}
