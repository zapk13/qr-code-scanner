package com.zapk13.qrscanner.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.zapk13.qrscanner.R

object LinkUtils {

    private val URL_PATTERN = Regex(
        "^(https?://)[\\w\\-]+(\\.[\\w\\-]+)+[/#?]?.*$",
        RegexOption.IGNORE_CASE
    )

    fun isOpenableUrl(text: String): Boolean {
        val trimmed = text.trim()
        if (trimmed.startsWith("http://", ignoreCase = true) ||
            trimmed.startsWith("https://", ignoreCase = true) ||
            trimmed.startsWith("www.", ignoreCase = true)
        ) {
            return true
        }
        return URL_PATTERN.matches(trimmed)
    }

    fun normalizeUrl(text: String): String {
        val trimmed = text.trim()
        return if (trimmed.startsWith("http://", ignoreCase = true) ||
            trimmed.startsWith("https://", ignoreCase = true)
        ) {
            trimmed
        } else {
            "https://$trimmed"
        }
    }

    fun openInBrowser(context: Context, text: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(normalizeUrl(text)))
        context.startActivity(intent)
    }

    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("QR content", text))
        Toast.makeText(context, R.string.copied, Toast.LENGTH_SHORT).show()
    }
}
