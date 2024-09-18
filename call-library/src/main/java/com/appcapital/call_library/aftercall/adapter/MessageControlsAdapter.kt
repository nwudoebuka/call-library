package com.appcapital.call_library.aftercall.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appcapital.call_library.R

class MessageControlsAdapter(private val items: List<String>, private val onSelectQuickMessage: (position: Int) -> Unit) :
    RecyclerView.Adapter<MessageControlsAdapter.ViewHolder>() {

    private var selectedPosition = -1 // Keeps track of the selected position

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val radioButton: RadioButton = itemView.findViewById(R.id.radio_button)
        val textView: TextView = itemView.findViewById(R.id.message_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_control_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.textView.text = item

        // Set the radio button checked state based on the selected position
        holder.radioButton.isChecked = position == selectedPosition

        // Handle radio button click
        holder.radioButton.setOnClickListener {
            selectedPosition = holder.adapterPosition
            onSelectQuickMessage(selectedPosition)
            notifyDataSetChanged() // Refresh the list to update the checked state
        }

        // Optionally handle item click (text click)
        holder.itemView.setOnClickListener {
            selectedPosition = holder.adapterPosition
            onSelectQuickMessage(selectedPosition)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = items.size
}