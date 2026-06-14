package com.openassist.ui.permissions

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun PermissionScreen(action: String, onAllow: () -> Unit, onDeny: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDeny,
        title = { Text("Confirm sensitive action") },
        text = { Text(action) },
        confirmButton = { Button(onClick = onAllow) { Text("Allow") } },
        dismissButton = { TextButton(onClick = onDeny) { Text("Deny") } },
    )
}
