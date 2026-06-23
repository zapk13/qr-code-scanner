package com.zapk13.qrscanner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zapk13.qrscanner.data.HistoryRepository
import com.zapk13.qrscanner.data.ScanRecord
import com.zapk13.qrscanner.databinding.ActivityHistoryBinding
import com.zapk13.qrscanner.util.LinkUtils

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var historyRepository: HistoryRepository
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        historyRepository = HistoryRepository(this)

        binding.toolbar.setNavigationOnClickListener { finish() }

        adapter = HistoryAdapter(::onHistoryItemClick)
        binding.historyList.layoutManager = LinearLayoutManager(this)
        binding.historyList.adapter = adapter

        binding.clearHistoryButton.setOnClickListener {
            confirmClearHistory()
        }

        loadHistory()
    }

    override fun onResume() {
        super.onResume()
        loadHistory()
    }

    private fun loadHistory() {
        val records = historyRepository.getAll()
        adapter.submitList(records)
        val isEmpty = records.isEmpty()
        binding.emptyView.isVisible = isEmpty
        binding.historyList.isVisible = !isEmpty
        binding.clearHistoryButton.isEnabled = !isEmpty
    }

    private fun onHistoryItemClick(record: ScanRecord) {
        if (LinkUtils.isOpenableUrl(record.content)) {
            MaterialAlertDialogBuilder(this)
                .setTitle(record.content)
                .setPositiveButton(R.string.open_in_browser) { _, _ ->
                    LinkUtils.openInBrowser(this, record.content)
                }
                .setNegativeButton(R.string.copy) { _, _ ->
                    LinkUtils.copyToClipboard(this, record.content)
                }
                .setNeutralButton(R.string.dismiss, null)
                .show()
        } else {
            LinkUtils.copyToClipboard(this, record.content)
        }
    }

    private fun confirmClearHistory() {
        MaterialAlertDialogBuilder(this)
            .setMessage(R.string.clear_history_confirm)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.clear) { _, _ ->
                historyRepository.clearAll()
                loadHistory()
            }
            .show()
    }
}
