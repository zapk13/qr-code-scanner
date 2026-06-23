package com.zapk13.qrscanner.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class HistoryRepository(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getAll(): List<ScanRecord> {
        purgeExpired()
        val json = prefs.getString(KEY_HISTORY, "[]") ?: "[]"
        val array = JSONArray(json)
        val records = mutableListOf<ScanRecord>()
        for (i in 0 until array.length()) {
            val item = array.getJSONObject(i)
            records.add(
                ScanRecord(
                    content = item.getString("content"),
                    timestamp = item.getLong("timestamp")
                )
            )
        }
        return records.sortedByDescending { it.timestamp }
    }

    fun add(content: String) {
        val trimmed = content.trim()
        if (trimmed.isEmpty()) return

        val records = getAll().toMutableList()
        if (records.any { it.content == trimmed }) {
            records.removeAll { it.content == trimmed }
        }
        records.add(0, ScanRecord(trimmed, System.currentTimeMillis()))
        save(records)
    }

    fun clearAll() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }

    private fun purgeExpired() {
        val cutoff = System.currentTimeMillis() - RETENTION_MS
        val json = prefs.getString(KEY_HISTORY, "[]") ?: "[]"
        val array = JSONArray(json)
        val kept = JSONArray()
        for (i in 0 until array.length()) {
            val item = array.getJSONObject(i)
            if (item.getLong("timestamp") >= cutoff) {
                kept.put(item)
            }
        }
        if (kept.length() != array.length()) {
            prefs.edit().putString(KEY_HISTORY, kept.toString()).apply()
        }
    }

    private fun save(records: List<ScanRecord>) {
        val array = JSONArray()
        records.forEach { record ->
            array.put(
                JSONObject()
                    .put("content", record.content)
                    .put("timestamp", record.timestamp)
            )
        }
        prefs.edit().putString(KEY_HISTORY, array.toString()).apply()
    }

    companion object {
        private const val PREFS_NAME = "scan_history"
        private const val KEY_HISTORY = "history"
        private const val RETENTION_DAYS = 30L
        private val RETENTION_MS = RETENTION_DAYS * 24 * 60 * 60 * 1000
    }
}
