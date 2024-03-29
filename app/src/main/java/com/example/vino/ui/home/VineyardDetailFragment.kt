package com.example.vino.ui.home

import android.animation.LayoutTransition
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
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
import com.example.vino.model.Vineyard
import com.example.vino.network.Daily
import com.example.vino.network.WeatherBasic
import com.example.vino.ui.ImageShimmer
import com.facebook.shimmer.ShimmerDrawable
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
        binding.weatherButton.layoutTransition.enableTransitionType(LayoutTransition.CHANGING) // animates when forecast set
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vineyard = vinoUserModel.selectedVineyard

        if (vineyard != null) {
            vineyardDetailFragmentViewModel.setVineyard(vineyard)

            setSharedViewTransitionName(vineyard.name)

            binding.vineyardNameEnd.text = vineyard.name
            binding.currentJobText.text = getString(R.string.current_job, vineyard.job)

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

            binding.weatherButton.setOnClickListener {
                val action = VineyardDetailFragmentDirections.actionVineyardDetailToWeatherDetailFragment()
                findNavController().navigate(action)
            }

            binding.mapButton.setOnClickListener {
                val action = VineyardDetailFragmentDirections.actionVineyardDetailToVineyardMapFragment(vineyard.vineyardId)
                findNavController().navigate(action)
            }

            binding.lwpButton.setOnClickListener {
                val action =
                    VineyardDetailFragmentDirections.actionVineyardDetailToLeafWaterPotentialFragment(
                        vineyard.vineyardId
                    )
                findNavController().navigate(action)
            }

            if (vineyard.sprayed)
                setSpray(vineyard)
            else
                binding.sprayCardView.visibility = View.GONE
        }
        setBackgroundImage(vineyard)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setBackgroundImage(vineyard: Vineyard?) {
        // imageView.load uses the singleton ImageLoader to enqueue an ImageRequest.
        // The singleton ImageLoader can be accessed using
        // val imageLoader = context.imageLoader
        // get image from cache
        if (vinoUserModel.imageCacheKey == null) {
            if (vineyard != null) {
                val imgUri = vineyard.imageUrl.toUri().buildUpon().scheme("https").build()

                // This is the placeholder for the imageView
                val shimmerDrawable = ShimmerDrawable().apply {
                    setShimmer(ImageShimmer().shimmer)
                }
                binding.vineyardImageEnd.load(imgUri) {
                    allowHardware(false)
                    placeholder(shimmerDrawable)
                    error(R.drawable.generic_vineyard)
                    listener { _, metadata ->
                        vinoUserModel.imageCacheKey = metadata.memoryCacheKey
                    }
                }
            }
        } else {
            lifecycleScope.launch(Dispatchers.Default) {
                val bitMap: Bitmap? = vinoUserModel.imageCacheKey?.let { context?.imageLoader?.memoryCache?.get(it) }
                activity?.runOnUiThread {
                    binding.vineyardImageEnd.setImageBitmap(bitMap)
                }
            }
        }
    }

    private fun setSpray(vineyard: Vineyard) {
        binding.sprayCardView.visibility = View.VISIBLE
        binding.sprayText.text = "${vineyard.type} - ${vineyard.material}"
        val rei = if (vineyard.rei == 0) {
            "None"
        } else {
            "${vineyard.rei.toString()} hrs"
        }
        binding.reiText.text = "REI: $rei"

        binding.sprayOrderButton.setOnClickListener {
            val pdfIntent: Intent = Intent().apply {
                action = Intent.ACTION_VIEW
                setDataAndType(Uri.parse(vineyard.sprayOrderUrl), "application/pdf")
            }

            val chooser: Intent = Intent.createChooser(pdfIntent, null)
            startActivity(chooser)
        }

    }

    private fun setForecast(weatherBasic: WeatherBasic?) {
        if (weatherBasic != null) {
            val dailyForecasts = weatherBasic.dailyTemperatures //should only be 8
            dailyForecasts?.forEachIndexed { index, daily ->
                if (index != 0) // don't do first which is today/current
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
        sdf.timeZone = TimeZone.getDefault()
        val weatherDate = sdf.format(Date(daily.dt * 1000L))
        date.text = weatherDate

        temp.text = getString(
            R.string.temperature_value_degreee,
            daily.temp.max.toInt()
        )
    }

    private fun inflateTransitions() {
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.vineyard_detail_transition)

        // TODO FIX return transtition
//        sharedElementReturnTransition = TransitionInflater.from(requireContext())
//            .inflateTransition(R.transition.vineyard_detail_transition)
    }

    private fun setSharedViewTransitionName(name: String?) {
        binding.vineyardItemCardViewEnd.transitionName = "vineyardCardView$name"
        binding.vineyardItemLinearLayoutEnd.transitionName = "vineyardLinearLayout$name"
        binding.vineyardImageEnd.transitionName = "vineyardImage$name"
        binding.vineyardNameEnd.transitionName = "vineyardName$name"
        binding.currentJobText.transitionName = "vineyardJob$name"
    }
}
