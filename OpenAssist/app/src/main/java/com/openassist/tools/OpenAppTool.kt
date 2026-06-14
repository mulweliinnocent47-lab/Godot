package com.openassist.tools

import android.content.Context
import android.content.Intent

class OpenAppTool(private val context: Context) : Tool {
    override val name = "open_app"
    override val description = "Launches an installed app by package name after confirmation."
    override val sensitive = true

    override suspend fun run(arguments: Map<String, String>): ToolResult {
        val packageName = arguments["package"] ?: return ToolResult(name, "Missing package argument.")
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            ?: return ToolResult(name, "No launch intent found for $packageName.")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return ToolResult(name, "Opened $packageName")
    }
}
