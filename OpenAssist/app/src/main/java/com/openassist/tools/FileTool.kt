package com.openassist.tools

class FileTool : Tool {
    override val name = "File"
    override val description = "Planned tool placeholder for future phone-control and local-file features."
    override val sensitive = true

    override suspend fun run(arguments: Map<String, String>) = ToolResult(
        name = name,
        output = "FileTool is planned for a future OpenAssist version and requires explicit user confirmation.",
    )
}
