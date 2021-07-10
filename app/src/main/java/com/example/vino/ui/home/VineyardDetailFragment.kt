package com.example.vino.ui.home

import android.graphics.Bitmap
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.imageLoader
import coil.load
import com.example.vino.R
import com.example.vino.VinoApplication
import com.example.vino.databinding.FragmentVineyardDetailBinding
import com.example.vino.model.UserViewModel
import com.example.vino.model.UserViewModelFactory
import com.example.vino.network.Daily
import com.example.vino.network.WeatherBasic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class VineyardDetailFragment : Fragment() {

    private val vineyardDetailFragmentViewModel: VineyardDetailFragmentViewModel by viewModels {
        VineyardDetailFragmentViewModelFactory((requireActivity().application as VinoApplication).repository)
    }
    private val vinoUserModel: UserViewModel by activityViewModels {
        UserViewModelFactory((requireActivity().application as VinoApplication).repository)
    }

    private var _binding: FragmentVineyardDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var vineyardId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflateTransitions()

        arguments?.let {
            vineyardId = it.getInt("vineyardId", 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentVineyardDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vineyard = vinoUserModel.selectedVineyard

        if (vineyard != null) {
            vineyardDetailFragmentViewModel.setVineyard(vineyard)

            setSharedViewTransitionName(vineyard.name)

            binding.vineyardNameEnd.text = vineyard.name

            vineyardDetailFragmentViewModel.weather.observe(viewLifecycleOwner, { weatherBasic ->

                binding.vineyardTempEnd.text = getString(
                    R.string.temperature_value_degreee,
                    weatherBasic.current.temp.toInt()
                )
                binding.vineyardHumidityEnd.text = getString(
                    R.string.humidity_value_percent,
                    weatherBasic.current.humidity.toInt()
                )

                setForecast(weatherBasic)
            })


            binding.mapButton.setOnClickListener {
                val action =
                    VineyardDetailFragmentDirections.actionVineyardDetailToVineyardMapFragment(
                        vineyard.vineyardId
                    )
                findNavController().navigate(action)
            }

            binding.lwpButton.setOnClickListener {
                val action =
                    VineyardDetailFragmentDirections.actionVineyardDetailToLeafWaterPotentialFragment(
                        vineyard.vineyardId
                    )
                findNavController().navigate(action)
            }
        }

        // imageView.load uses the singleton ImageLoader to enqueue an ImageRequest.
        // The singleton ImageLoader can be accessed using
        // val imageLoader = context.imageLoader
        // get image from cache
        lifecycleScope.launch(Dispatchers.Default) {
            val bitMap: Bitmap? =
                vinoUserModel.imageCacheKey?.let { context?.imageLoader?.memoryCache?.get(it) } // TODO: if null set to placeholder then load? when click quick not enough time to load
            activity?.runOnUiThread {
                binding.vineyardImageEnd.setImageBitmap(bitMap)
            }
        }
    }

    private fun setForecast(weatherBasic: WeatherBasic?) {
        if (weatherBasic != null) {
            val dailyForecasts = weatherBasic.dailyTemperatures //should only be 8
            dailyForecasts.forEachIndexed { index, daily ->
                if (index != 0) // don't do first which is today
                    setWeatherForDay(index, daily)
            }
        }
    }

    private fun setWeatherForDay(index: Int, daily: Daily) {
        when (index) {
            1 -> setWeather(binding.day1date, binding.day1icon, binding.day1temp, daily)
            2 -> setWeather(binding.day2date, binding.day2icon, binding.day2temp, daily)
            3 -> setWeather(binding.day3date, binding.day3icon, binding.day3temp, daily)
            4 -> setWeather(binding.day4date, binding.day4icon, binding.day4temp, daily)
            5 -> setWeather(binding.day5date, binding.day5icon, binding.day5temp, daily)
            6 -> setWeather(binding.day6date, binding.day6icon, binding.day6temp, daily)
            7 -> setWeather(binding.day7date, binding.day7icon, binding.day7temp, daily)
        }
    }

    private fun setWeather(date: TextView, icon: ImageView, temp: TextView, daily: Daily) {
        val sdf = SimpleDateFormat("M/d", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val weatherDate = sdf.format(Date(daily.dt * 1000L))
        date.text = weatherDate

        temp.text = getString(
            R.string.temperature_value_degreee,
            daily.temp.max.toInt()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun inflateTransitions() {
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.vineyard_detail_transition)

        sharedElementReturnTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.vineyard_detail_transition)
    }

    private fun setSharedViewTransitionName(name: String?) {
        binding.vineyardItemCardViewEnd.transitionName = "vineyardCardView$name"
        binding.vineyardItemLinearLayoutEnd.transitionName = "vineyardLinearLayout$name"
        binding.vineyardImageEnd.transitionName = "vineyardImage$name"
        binding.vineyardNameEnd.transitionName = "vineyardName$name"
        binding.vineyardTempEnd.transitionName = "vineyardTemperature$name"
        binding.vineyardHumidityEnd.transitionName = "vineyardHumidity$name"
    }
}
