package com.openassist.tools

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock

class AlarmTool(private val context: Context) : Tool {
    override val name = "set_alarm"
    override val description = "Opens Android's alarm UI to create an alarm at the requested hour and minute."
    override val sensitive = true
    override val arguments = listOf(
        ToolArgument("hour", "Alarm hour in 24-hour time, from 0 to 23.", true),
        ToolArgument("minute", "Alarm minute, from 0 to 59.", true),
        ToolArgument("message", "Optional alarm label."),
    )

    override suspend fun run(arguments: Map<String, String>): ToolResult {
        val hour = arguments["hour"]?.toIntOrNull() ?: return ToolResult(name, "Missing or invalid hour.")
        val minute = arguments["minute"]?.toIntOrNull() ?: return ToolResult(name, "Missing or invalid minute.")
        if (hour !in 0..23 || minute !in 0..59) return ToolResult(name, "Alarm time is out of range.")
        val intent = Intent(AlarmClock.ACTION_SET_ALARM)
            .putExtra(AlarmClock.EXTRA_HOUR, hour)
            .putExtra(AlarmClock.EXTRA_MINUTES, minute)
            .putExtra(AlarmClock.EXTRA_MESSAGE, arguments["message"].orEmpty())
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return ToolResult(name, "Opened alarm confirmation for %02d:%02d.".format(hour, minute))
    }
}
