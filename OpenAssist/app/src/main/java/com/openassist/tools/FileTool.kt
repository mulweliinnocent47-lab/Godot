package com.openassist.tools

import android.content.Context
import java.io.File

class FileTool(private val context: Context) : Tool {
    override val name = "file"
    override val description = "Lists, reads, creates, modifies, or deletes files under OpenAssist's local files directory."
    override val sensitive = true
    override val arguments = listOf(
        ToolArgument("action", "One of list, read, create, modify, delete.", true),
        ToolArgument("path", "Relative path inside the app's local files directory."),
        ToolArgument("content", "File content for create or modify actions."),
    )

    override suspend fun run(arguments: Map<String, String>): ToolResult {
        val action = arguments["action"].orEmpty()
        val target = resolveSafeFile(arguments["path"].orEmpty())
            ?: return ToolResult(name, "Invalid path. Use a relative path inside local app storage.")
        return when (action) {
            "list" -> {
                val files = target.takeIf { it.isDirectory }?.listFiles().orEmpty()
                    .sortedBy { it.name }
                    .joinToString("\n") { if (it.isDirectory) "${it.name}/" else it.name }
                ToolResult(name, files.ifBlank { "No files found." })
            }
            "read" -> if (target.isFile) ToolResult(name, target.readText()) else ToolResult(name, "File not found.")
            "create", "modify" -> {
                target.parentFile?.mkdirs()
                target.writeText(arguments["content"].orEmpty())
                ToolResult(name, "Wrote ${target.name}.")
            }
            "delete" -> if (target.delete()) ToolResult(name, "Deleted ${target.name}.") else ToolResult(name, "File not found or could not be deleted.")
            else -> ToolResult(name, "Unknown file action: $action.")
        }
    }

    private fun resolveSafeFile(relativePath: String): File? {
        val base = context.filesDir.canonicalFile
        val target = if (relativePath.isBlank()) base else File(base, relativePath).canonicalFile
        return target.takeIf { it.path == base.path || it.path.startsWith("${base.path}${File.separator}") }
    }
}
