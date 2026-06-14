package com.openassist.tools

class ContactTool : Tool {
    override val name = "Contact"
    override val description = "Planned tool placeholder for future phone-control and local-file features."
    override val sensitive = true

    override suspend fun run(arguments: Map<String, String>) = ToolResult(
        name = name,
        output = "ContactTool is planned for a future OpenAssist version and requires explicit user confirmation.",
    )
}
