package com.openassist.tools

import android.content.Context
import android.content.Intent
import android.net.Uri

class SmsTool(private val context: Context) : Tool {
    override val name = "send_sms"
    override val description = "Opens the SMS app with a recipient and message drafted for user review."
    override val sensitive = true
    override val arguments = listOf(
        ToolArgument("phone", "Recipient phone number.", true),
        ToolArgument("message", "SMS message body.", true),
    )

    override suspend fun run(arguments: Map<String, String>): ToolResult {
        val phone = arguments["phone"].orEmpty()
        val message = arguments["message"].orEmpty()
        if (phone.isBlank()) return ToolResult(name, "Missing phone argument.")
        if (message.isBlank()) return ToolResult(name, "Missing message argument.")
        val intent = Intent(Intent.ACTION_SENDTO)
            .setData(Uri.parse("smsto:$phone"))
            .putExtra("sms_body", message)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return ToolResult(name, "Opened SMS draft for $phone.")
    }
}
