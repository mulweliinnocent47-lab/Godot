package com.openassist.tools

import com.openassist.data.openrouter.ToolDefinition
import com.openassist.data.openrouter.ToolFunction
import com.openassist.data.openrouter.ToolParameterProperty
import com.openassist.data.openrouter.ToolParameters

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

    fun toolDefinitions(): List<ToolDefinition> = availableTools().map { tool ->
        ToolDefinition(
            function = ToolFunction(
                name = tool.name,
                description = tool.description,
                parameters = ToolParameters(
                    properties = tool.arguments.associate { argument ->
                        argument.name to ToolParameterProperty(
                            type = "string",
                            description = argument.description,
                        )
                    },
                    required = tool.arguments.filter { it.required }.map { it.name },
                ),
            ),
        )
    }
}
