package com.example.vino.ui.home

import android.graphics.Bitmap
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.imageLoader
import coil.memory.MemoryCache
import com.example.vino.R
import com.example.vino.databinding.FragmentBlankBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BlankFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlankFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentBlankBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var name: String
    private var imageCacheKey: MemoryCache.Key? = null // For the clicked vineyard image

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.vineyard_detail_transition)

        sharedElementReturnTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.vineyard_detail_transition)

        arguments?.let {
            name = it.getString("vineyardName").toString()
            imageCacheKey = it.getParcelable("vineyardImage")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBlankBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vineyardItemCardViewEnd.transitionName = "vineyardCardView$name"
        binding.vineyardItemLinearLayoutEnd.transitionName = "vineyardLinearLayout$name"
        binding.vineyardImageEnd.transitionName = "vineyardImage$name"
        binding.vineyardNameEnd.transitionName = "vineyardName$name"
        binding.vineyardTempEnd.transitionName = "vineyardTemperature$name"
        binding.vineyardHumidityEnd.transitionName = "vineyardHumidity$name"

        binding.vineyardNameEnd.text = name

        // imageView.load uses the singleton ImageLoader to enqueue an ImageRequest.
        // The singleton ImageLoader can be accessed using
        // val imageLoader = context.imageLoader

        lifecycleScope.launch(Dispatchers.Default) {
            val bitMap: Bitmap? = imageCacheKey?.let { context?.imageLoader?.memoryCache?.get(it) } // TODO: if null set to placeholder then load? when click quick not enough time to load
            activity?.runOnUiThread {
                binding.vineyardImageEnd.setImageBitmap(bitMap)
            }
        }

        binding.vineyardTempEnd.text = getString(R.string.temperature_value_degreee, 86)
        binding.vineyardHumidityEnd.text = getString(R.string.humidity_value_percent, 30)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}