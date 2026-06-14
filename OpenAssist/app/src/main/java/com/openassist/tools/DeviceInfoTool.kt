package com.openassist.tools

import android.os.Build

class DeviceInfoTool : Tool {
    override val name = "device_info"
    override val description = "Returns Android version, device, and manufacturer details."
    override val sensitive = false

    override suspend fun run(arguments: Map<String, String>) = ToolResult(
        name = name,
        output = "Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT}) on ${Build.MANUFACTURER} ${Build.MODEL}",
    )
}
