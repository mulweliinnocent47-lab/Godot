package com.openassist.tools

import android.content.Context

class InstalledAppsTool(private val context: Context) : Tool {
    override val name = "installed_apps"
    override val description = "Lists launchable installed applications."
    override val sensitive = false

    override suspend fun run(arguments: Map<String, String>): ToolResult {
        val packageManager = context.packageManager
        val apps = packageManager.getInstalledApplications(0)
            .map { packageManager.getApplicationLabel(it).toString() }
            .distinct()
            .sorted()
            .take(100)
        return ToolResult(name, apps.joinToString("\n"))
    }
}
