package com.example.vino.ui.home

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.imageLoader
import com.example.vino.R
import com.example.vino.VinoApplication
import com.example.vino.databinding.FragmentVineyardDetailBinding
import com.example.vino.model.UserViewModel
import com.example.vino.model.UserViewModelFactory
import com.example.vino.model.Vineyard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class VineyardDetailFragment : Fragment() {

    private val vinoUserModel: UserViewModel by activityViewModels {
        UserViewModelFactory((requireActivity().application as VinoApplication).repository)
    }

    private var _binding: FragmentVineyardDetailBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var vineyardId: Int = 0
    private var vineyard: Vineyard? = null

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

        vinoUserModel.setSelectedVineyard(vineyardId)
        vineyard = vinoUserModel.currentVineyard

        setSharedViewTransitionName(vineyard?.name)

        binding.vineyardNameEnd.text = vineyard?.name ?: "No Vineyard"

        // imageView.load uses the singleton ImageLoader to enqueue an ImageRequest.
        // The singleton ImageLoader can be accessed using
        // val imageLoader = context.imageLoader
        // get image from cache
        lifecycleScope.launch(Dispatchers.Default) {
            val bitMap: Bitmap? = vinoUserModel.imageCacheKey?.let { context?.imageLoader?.memoryCache?.get(it) } // TODO: if null set to placeholder then load? when click quick not enough time to load
            activity?.runOnUiThread {
                binding.vineyardImageEnd.setImageBitmap(bitMap)
            }
        }

        binding.vineyardTempEnd.text = getString(R.string.temperature_value_degreee,
            vineyard?.temperature
        )
        binding.vineyardHumidityEnd.text = getString(R.string.humidity_value_percent,
            vineyard?.humidity
        )

        //animateButtonTransitionToSpinner(binding.mapButton)

        binding.mapButton.setOnClickListener {
            val action = VineyardDetailFragmentDirections.actionVineyardDetailToVineyardMapFragment()
            findNavController().navigate(action)
        }
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

    private fun setCornerRadius(cornerRadius: Float, view: View) {
        val shape = GradientDrawable()
        shape.cornerRadius = cornerRadius
        shape.setColor(ContextCompat.getColor(view.context, R.color.purple_600))
        view.background = shape
    }

    private fun animateButtonTransitionToSpinner(button: Button) {

        button.text = null

        val animationSet = AnimatorSet()
        val animationSubset1 = AnimatorSet()
        val duration = 2000.toLong()
        animationSubset1.playTogether(getCornerRadiusAnimation(button).setDuration(duration), getWidthAnimation(button).setDuration(duration), getHeightAnimation(button).setDuration(duration))


        animationSet.playSequentially(
            animationSubset1
        )
        activity?.runOnUiThread {
            animationSet.start()
            button.text = "View Map"
        }
    }

    private fun getCornerRadiusAnimation(button: Button): ValueAnimator {
        var screenDensity = resources.displayMetrics.density
        var buttonWidth = (resources.displayMetrics.widthPixels - 40) / screenDensity

        var targetCornerRadius = buttonWidth * 0.5f
        // 1. Convert button into a circle
        val anim = ValueAnimator.ofFloat(targetCornerRadius, 8f)
        anim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Float
            setCornerRadius(value, button)
        }
        return anim
    }

    private fun getWidthAnimation(button: Button): ValueAnimator {
        var screenDensity = resources.displayMetrics.density
        var initialWidth = (resources.displayMetrics.widthPixels / screenDensity) - (40 * screenDensity)


        val anim = ValueAnimator.ofInt((16 * screenDensity).roundToInt(), initialWidth.roundToInt())
        anim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val params = button.layoutParams
            params.width = value
            button.layoutParams = params
        }
        return anim
    }

    private fun getHeightAnimation(button: Button): ValueAnimator {
        val screenDensity = resources.displayMetrics.density
        var initialHeight = 44 * screenDensity

        val anim = ValueAnimator.ofInt((16 * screenDensity).roundToInt(), initialHeight.roundToInt())
        anim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val params = button.layoutParams
            params.height = value
            button.layoutParams = params
        }
        return anim
    }

}