package com.example.vino

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vino.ui.adapter.AlertRecyclerViewAdapter
import com.example.vino.ui.adapter.WeatherForecastRecyclerViewAdapter

class WeatherDetailFragment : Fragment() {

    companion object {
        fun newInstance() = WeatherDetailFragment()
    }

    private lateinit var viewModel: WeatherDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather_draft, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WeatherDetailViewModel::class.java)
        val rv = view?.findViewById<RecyclerView>(R.id.forecastRecyclerView)
        val arv = view?.findViewById<RecyclerView>(R.id.alertRecyclerView)
        if (rv != null) {
            rv.setHasFixedSize(true)
            rv.adapter = WeatherForecastRecyclerViewAdapter()
        }
        if (arv != null) {
            arv.adapter = AlertRecyclerViewAdapter()
        }

        // TODO: Use the ViewModel
    }

}