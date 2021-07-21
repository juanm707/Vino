package com.example.vino.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vino.R
import com.example.vino.network.Alert
import java.text.SimpleDateFormat
import java.util.*

class AlertRecyclerViewAdapter(private val alerts: List<Alert>) : RecyclerView.Adapter<AlertRecyclerViewAdapter.AlertViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.alert_recycler_view_item, parent, false)
        return AlertViewHolder(layout)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert = alerts[position]
        holder.event.text = alert.event
        holder.time.text = getAlertDuration(alert.start, alert.end)
        holder.description.text = alert.description
    }

    override fun getItemCount(): Int {
        return alerts.size
    }

    class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val event: TextView = itemView.findViewById(R.id.eventName)
        val time: TextView = itemView.findViewById(R.id.timeText)
        val description: TextView = itemView.findViewById(R.id.descriptionText)
    }

    private fun getAlertDuration(start: Long, end: Long): String {
        return "${getAlertTime(start)} - ${getAlertTime(end)}"
    }

    private fun getAlertTime(time: Long): String {
        val sdf = SimpleDateFormat("E M/d h:mm a", Locale.US)
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(Date(time * 1000L))
    }
}