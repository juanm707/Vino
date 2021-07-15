package com.example.vino.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vino.R

class WeatherForecastRecyclerViewAdapter() : RecyclerView.Adapter<WeatherForecastRecyclerViewAdapter.ForecastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.forecast_recycler_view_item, parent, false)
        return ForecastViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 20
    }

    class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}