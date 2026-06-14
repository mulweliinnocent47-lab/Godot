package com.openassist.ui.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.openassist.ui.settings.SettingsScreen
import com.openassist.viewmodel.ChatViewModel
import com.openassist.viewmodel.SettingsViewModel

@Composable
fun ChatScreen(chatViewModel: ChatViewModel, settingsViewModel: SettingsViewModel) {
    val state by chatViewModel.state.collectAsState()
    var input by remember { mutableStateOf("") }
    var showSettings by remember { mutableStateOf(false) }

    if (showSettings) {
        SettingsScreen(settingsViewModel, onBack = { showSettings = false })
        return
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("OpenAssist", style = MaterialTheme.typography.headlineSmall)
            TextButton(onClick = { showSettings = true }) { Text("Settings") }
        }
        LazyColumn(Modifier.weight(1f).fillMaxWidth()) {
            items(state.messages) { message ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(message.role.uppercase(), style = MaterialTheme.typography.labelSmall)
                        Text(message.content)
                    }
                }
            }
        }
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        state.pendingTool?.let { pendingTool ->
            Card(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Text("Confirm tool action", style = MaterialTheme.typography.titleMedium)
                    Text("${pendingTool.name}: ${pendingTool.arguments}")
                    Row {
                        Button(onClick = { chatViewModel.respondToPendingTool(true) }) { Text("Allow") }
                        Spacer(Modifier.width(8.dp))
                        OutlinedButton(onClick = { chatViewModel.respondToPendingTool(false) }) { Text("Deny") }
                    }
                }
            }
        }
        if (state.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
        Row(Modifier.fillMaxWidth()) {
            OutlinedTextField(value = input, onValueChange = { input = it }, modifier = Modifier.weight(1f), label = { Text("Message") })
            Spacer(Modifier.width(8.dp))
            Button(onClick = { chatViewModel.send(input); input = "" }, enabled = !state.loading) { Text("Send") }
        }
    }
}
