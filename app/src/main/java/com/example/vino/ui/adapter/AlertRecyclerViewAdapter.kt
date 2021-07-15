package com.example.vino.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vino.R

class AlertRecyclerViewAdapter() : RecyclerView.Adapter<AlertRecyclerViewAdapter.AlertViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.alert_recycler_view_item, parent, false)
        return AlertViewHolder(layout)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 1
    }

    class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}