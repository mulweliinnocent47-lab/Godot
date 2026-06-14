package com.openassist.tools

import android.content.Context
import android.content.Intent
import android.net.Uri

class CallTool(private val context: Context) : Tool {
    override val name = "make_call"
    override val description = "Opens the phone dialer with a requested phone number."
    override val sensitive = true
    override val arguments = listOf(ToolArgument("phone", "Phone number to place in the dialer.", true))

    override suspend fun run(arguments: Map<String, String>): ToolResult {
        val phone = arguments["phone"].orEmpty()
        if (phone.isBlank()) return ToolResult(name, "Missing phone argument.")
        val intent = Intent(Intent.ACTION_DIAL)
            .setData(Uri.parse("tel:$phone"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return ToolResult(name, "Opened dialer for $phone.")
    }
}
