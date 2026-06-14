package com.openassist.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(onSaveApiKey: (String) -> Unit) {
    var apiKey by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("OpenAssist", style = MaterialTheme.typography.headlineLarge)
        Text("Bring your own OpenRouter API key. Requests go directly from this device to OpenRouter; there are no accounts, backend servers, Firebase, Supabase, or subscriptions.")
        Spacer(Modifier.height(16.dp))
        Text("OpenRouter usage may cost money depending on your chosen model. You control and store the key locally.")
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = apiKey,
            onValueChange = { apiKey = it },
            label = { Text("OpenRouter API key") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = { onSaveApiKey(apiKey) }, enabled = apiKey.isNotBlank()) { Text("Save key locally") }
    }
}
