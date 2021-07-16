package com.example.vino.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vino.R
import com.example.vino.network.Daily
import com.example.vino.network.Forecast
import com.example.vino.network.Hourly

class WeatherForecastRecyclerViewAdapter(private val forecasts: List<Forecast>, private val context: Context) : RecyclerView.Adapter<WeatherForecastRecyclerViewAdapter.ForecastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.forecast_recycler_view_item, parent, false)
        return ForecastViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val forecast = forecasts[position]
        holder.date.text = forecast.time()
        holder.temp.text = context.getString(R.string.temperature_value_degreee, forecast.temp().toInt())
    }

    override fun getItemCount(): Int {
        return forecasts.size
    }

    class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.dayDate)
        val icon: ImageView = itemView.findViewById(R.id.dayIcon)
        val temp: TextView = itemView.findViewById(R.id.dayTemp)
    }
}