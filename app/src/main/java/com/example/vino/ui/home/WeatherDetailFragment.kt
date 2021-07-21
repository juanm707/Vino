package com.example.vino.ui.home

import android.animation.LayoutTransition
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.vino.R
import com.example.vino.VinoApplication
import com.example.vino.databinding.FragmentWeatherDetailBinding
import com.example.vino.model.UserViewModel
import com.example.vino.model.UserViewModelFactory
import com.example.vino.model.Vineyard
import com.example.vino.network.Alert
import com.example.vino.network.WeatherBasic
import com.example.vino.ui.adapter.AlertRecyclerViewAdapter
import com.example.vino.ui.adapter.WeatherForecastRecyclerViewAdapter
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.*

enum class WeatherCode {
    GREY, SMOKE, SNOW, CLEAR, CLOUDS
}

class WeatherDetailFragment : Fragment() {

    private val weatherViewModel: WeatherDetailViewModel by viewModels {
        WeatherDetailViewModelFactory((requireActivity().application as VinoApplication).repository)
    }

    private val vinoUserModel: UserViewModel by activityViewModels {
        UserViewModelFactory((requireActivity().application as VinoApplication).repository)
    }

    private var _binding: FragmentWeatherDetailBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeatherDetailBinding.inflate(inflater, container, false)
        binding.forecastCardView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        binding.weatherHeader.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vineyard = vinoUserModel.selectedVineyard

        if (vineyard != null) {
            weatherViewModel.setVineyard(vineyard)

            binding.temperatureMapButton.setOnClickListener {
                val action = WeatherDetailFragmentDirections.actionWeatherDetailFragmentToVineyardMapFragment(vineyard.vineyardId, true)
                findNavController().navigate(action)
            }
        }

        binding.forecastRecyclerView.adapter = WeatherForecastRecyclerViewAdapter(listOf(), requireContext())

        weatherViewModel.vineyard.observe(viewLifecycleOwner, { vineyard ->
            setVineyardInfo(vineyard)
        })

        weatherViewModel.weather.observe(viewLifecycleOwner, { weather ->
            setWeatherInfo(weather)
            setAlerts(weather.alerts)

            // Initial recycler view items on hourly tab
            if (weather.hourlyTemperatures != null)
                binding.forecastRecyclerView.adapter = WeatherForecastRecyclerViewAdapter(weather.hourlyTemperatures.subList(0, 25), requireContext())

            setTabSelectedListener(weather)
        })
    }

    private fun setAlerts(alerts: List<Alert>?) {
        if (alerts != null) {
            binding.noAlertsIcon.visibility = View.GONE
            binding.noAlertsText.visibility = View.GONE
            binding.alertRecyclerView.adapter = AlertRecyclerViewAdapter(alerts)

        }
        else {
            binding.noAlertsIcon.visibility = View.VISIBLE
            binding.noAlertsText.visibility = View.VISIBLE
        }
    }

    private fun setTabSelectedListener(weather: WeatherBasic) {
        binding.tabLayoutWeather.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    if (tab.position == 0) {
                        if (weather.hourlyTemperatures != null)
                            binding.forecastRecyclerView.adapter = WeatherForecastRecyclerViewAdapter(weather.hourlyTemperatures.subList(0, 25), requireContext())
                    }
                    else
                        if (weather.hourlyTemperatures != null)
                            binding.forecastRecyclerView.adapter = WeatherForecastRecyclerViewAdapter(weather.dailyTemperatures.drop(1), requireContext())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }

    private fun setWeatherInfo(weather: WeatherBasic) {
        binding.temperature.text = weather.current.temp.toInt().toString()
        val (description, backgroundCode) = getWeatherDescription(weather.current.weather[0].description)
        binding.description.text = description
        binding.mainConstraintLayout.background = getWeatherBackground(backgroundCode)
        binding.sunriseText.text = getSunTime(weather.current.sunrise)
        binding.sunsetText.text = getSunTime(weather.current.sunset)
        binding.windSpeedText.text = getString(R.string.wind_speed, weather.current.wind_speed.toInt())
        binding.humidityText.text = getString(R.string.humidity_value, weather.current.humidity.toInt())
        binding.feelsLikeText.text = getString(R.string.feels_like, weather.current.feels_like.toInt())
        binding.cloudText.text = getString(R.string.clouds_percent, weather.current.clouds.toInt())
    }

    private fun getWeatherBackground(backgroundCode: WeatherCode): Drawable? {
        // TODO add smoke and snow
        return when (backgroundCode) {
            WeatherCode.GREY -> ContextCompat.getDrawable(requireContext(), R.drawable.weather_clouds)
            WeatherCode.CLOUDS -> ContextCompat.getDrawable(requireContext(), R.drawable.weather_sunny_cloudy)
            else -> ContextCompat.getDrawable(requireContext(), R.drawable.weather_sunny_clear)
        }
    }

    private fun getSunTime(sunTime: Long): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.US)
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(Date(sunTime * 1000L))
    }

    private fun getWeatherDescription(description: String): Pair<String, WeatherCode> {
        var weatherKeyWord = WeatherCode.CLEAR
        val splited = description.split(' ')
        val capitalized = splited.map { word ->
            if (word != "with" && word != "and") {
                val capitalWord = word.replaceFirstChar { it.uppercase() }
                weatherKeyWord = when(capitalWord) {
                    "Thunderstorm" -> WeatherCode.GREY
                    "Drizzle" -> WeatherCode.GREY
                    "Rain" -> WeatherCode.GREY
                    "Fog" -> WeatherCode.GREY
                    "Smoke" -> WeatherCode.SMOKE
                    "Snow" -> WeatherCode.SNOW
                    "Clear" -> WeatherCode.CLEAR
                    "Clouds" -> WeatherCode.CLOUDS
                    else -> weatherKeyWord
                }
                return@map capitalWord
            } else
                return@map word
        }
        return Pair(capitalized.joinToString(separator = " "), weatherKeyWord)
    }

    private fun setVineyardInfo(vineyard: Vineyard) {
        // TODO set city, state
        binding.vineyard.text = vineyard.name
        binding.town.text = "${vineyard.city}, ${vineyard.state}"
    }
}
