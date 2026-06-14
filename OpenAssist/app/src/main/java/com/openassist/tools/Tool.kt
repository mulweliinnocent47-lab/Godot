package com.openassist.tools

data class ToolRequest(val name: String, val arguments: Map<String, String> = emptyMap())
data class ToolResult(val name: String, val output: String, val requiresConfirmation: Boolean = false)
data class ToolArgument(val name: String, val description: String, val required: Boolean = false)

interface Tool {
    val name: String
    val description: String
    val sensitive: Boolean
    val arguments: List<ToolArgument>
        get() = emptyList()
    suspend fun run(arguments: Map<String, String>): ToolResult
}
