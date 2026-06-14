package com.openassist.tools

class ToolEngine(tools: List<Tool>) {
    private val registry = tools.associateBy { it.name }

    suspend fun execute(request: ToolRequest, confirmed: Boolean = false): ToolResult {
        val tool = registry[request.name] ?: return ToolResult(request.name, "Unknown tool: ${request.name}")
        if (tool.sensitive && !confirmed) {
            return ToolResult(tool.name, "User confirmation required before running ${tool.name}.", true)
        }
        return tool.run(request.arguments)
    }

    fun availableTools(): List<Tool> = registry.values.toList()
}
