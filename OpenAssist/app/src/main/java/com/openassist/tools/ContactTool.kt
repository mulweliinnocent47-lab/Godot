package com.openassist.tools

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.core.content.ContextCompat

class ContactTool(private val context: Context) : Tool {
    override val name = "read_contacts"
    override val description = "Searches local contacts by name and returns matching names and phone numbers."
    override val sensitive = true
    override val arguments = listOf(ToolArgument("query", "Contact name search text. Leave blank to list recent contacts."))

    override suspend fun run(arguments: Map<String, String>): ToolResult {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            return ToolResult(name, "READ_CONTACTS permission has not been granted.")
        }
        val query = arguments["query"].orEmpty()
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
        )
        val selection = if (query.isBlank()) null else "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?"
        val selectionArgs = if (query.isBlank()) null else arrayOf("%$query%")
        val contacts = mutableListOf<String>()
        context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        )?.use { cursor ->
            val nameIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (cursor.moveToNext() && contacts.size < 25) {
                contacts += "${cursor.getString(nameIndex)}: ${cursor.getString(numberIndex)}"
            }
        }
        return ToolResult(name, contacts.ifEmpty { listOf("No contacts found.") }.joinToString("\n"))
    }
}
