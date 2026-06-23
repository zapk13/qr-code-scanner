package com.zapk13.qrscanner

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zapk13.qrscanner.data.ScanRecord
import com.zapk13.qrscanner.databinding.ItemHistoryBinding
import java.text.DateFormat
import java.util.Date

class HistoryAdapter(
    private val onItemClick: (ScanRecord) -> Unit
) : ListAdapter<ScanRecord, HistoryAdapter.ViewHolder>(DiffCallback()) {

    private val dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(record: ScanRecord) {
            binding.contentText.text = record.content
            binding.dateText.text = dateFormat.format(Date(record.timestamp))
            binding.root.setOnClickListener { onItemClick(record) }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<ScanRecord>() {
        override fun areItemsTheSame(oldItem: ScanRecord, newItem: ScanRecord): Boolean {
            return oldItem.content == newItem.content && oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(oldItem: ScanRecord, newItem: ScanRecord): Boolean {
            return oldItem == newItem
        }
    }
}
