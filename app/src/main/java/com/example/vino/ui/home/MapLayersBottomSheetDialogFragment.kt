package com.example.vino.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.vino.R
import com.example.vino.databinding.MapLayerSelectBottomSheetBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.card.MaterialCardView

enum class MapLayer {
    NONE, TEMPERATURE, WIND, RAIN
}

class MapLayersBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var _binding: MapLayerSelectBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var currentMapType: Int = -1
    private var currentMapDetail: Int = -1

    private var mListener: MapItemClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentMapType = arguments?.getInt(MAP_TYPE) ?: -1
        currentMapDetail = arguments?.getInt(MAP_DETAIL) ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MapLayerSelectBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeMapBottomSheet.setOnClickListener {
            dismiss()
        }

        setUpSelectedItems()
        setUpItemClicks()
    }

    private fun setUpSelectedItems() {
        setSelectedMapType(currentMapType)
        setSelectedMapDetail(MapLayer.values()[currentMapDetail])
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setListener(listener: MapItemClickListener) {
        mListener = listener
    }

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle): MapLayersBottomSheetDialogFragment {
            val fragment = MapLayersBottomSheetDialogFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private fun setUpItemClicks() {
        if (mListener != null) {
            binding.defaultMapCardView.setOnClickListener {
                selectCardView(binding.defaultMapCardView, binding.defaultText, type = MAP_TYPE, 1)
                mListener?.onMapTypeItemClick(1)
            }
            binding.satelliteMapCardView.setOnClickListener {
                selectCardView(binding.satelliteMapCardView, binding.satelliteText, type = MAP_TYPE, 4)
                mListener?.onMapTypeItemClick(4)
            }
            binding.terrainMapCardView.setOnClickListener {
                selectCardView(binding.terrainMapCardView, binding.terrainText, type = MAP_TYPE,  3)
                mListener?.onMapTypeItemClick(3)
            }

            binding.temperatureMapCardView.setOnClickListener {
                selectCardView(binding.temperatureMapCardView, binding.temperatureText, type = MAP_DETAIL, 1)
                mListener?.onMapDetailItemClick(MapLayer.TEMPERATURE)
            }
            binding.windMapCardView.setOnClickListener {
                selectCardView(binding.windMapCardView, binding.windText, type = MAP_DETAIL, 2)
                mListener?.onMapDetailItemClick(MapLayer.WIND)
            }
            binding.rainMapCardView.setOnClickListener {
                selectCardView(binding.rainMapCardView, binding.rainText, type = MAP_DETAIL, 3)
                mListener?.onMapDetailItemClick(MapLayer.RAIN)
            }
        }
    }

    private fun selectCardView(cardView: MaterialCardView, textView: TextView, type: String? = null, value: Int) {
        if (type == MAP_TYPE) {
            if (currentMapType != value) {
                when(currentMapType) {
                    1 -> clearSelected(binding.defaultMapCardView, binding.defaultText) // clear default
                    3 -> clearSelected(binding.terrainMapCardView, binding.terrainText)  // clear terrain
                    4 -> clearSelected(binding.satelliteMapCardView, binding.satelliteText) // clear satellite
                }
                currentMapType = value
                setSelected(cardView, textView)
            }
        } else if (type == MAP_DETAIL) {
            when (currentMapDetail) {
                1 -> clearSelected(binding.temperatureMapCardView, binding.temperatureText)
                2 -> clearSelected(binding.windMapCardView, binding.windText)
                3 -> clearSelected(binding.rainMapCardView, binding.rainText)
            }
            if (currentMapDetail == value) { // clicked on the same one, unselect
                currentMapDetail = 0 // set to none selected
            } else {
                currentMapDetail = value
                setSelected(cardView, textView)
            }
        } else {
            setSelected(cardView, textView)
        }
    }

    private fun setSelected(cardView: MaterialCardView, textView: TextView) {
        val selectColor = ContextCompat.getColor(requireContext(), R.color.map_type_select)
        cardView.strokeColor = selectColor
        cardView.strokeWidth = (2 * resources.displayMetrics.density).toInt()
        textView.setTextColor(selectColor)
    }

    private fun clearSelected(clickedCardView: MaterialCardView, textView: TextView) {
        val normalColor = ContextCompat.getColor(requireContext(), R.color.greyMedium)
        textView.setTextColor(normalColor)
        clickedCardView.strokeWidth = 0
        clickedCardView.invalidate()
    }

    // Following two methods are used in the beginning to highlight what is already selected
    private fun setSelectedMapType(currentMapType: Int) {
        when (currentMapType) { // pass null for type since this is called at the start of the fragment, there would not be any selected
            1 -> selectCardView(binding.defaultMapCardView, binding.defaultText, null, 1) // 1 is normal/default
            3 -> selectCardView(binding.terrainMapCardView, binding.terrainText, null, 3) // 3 is terrain
            4 -> selectCardView(binding.satelliteMapCardView, binding.satelliteText, null, 4) // 4 is satellite
        }
    }

    private fun setSelectedMapDetail(currentMapDetail: MapLayer) {
        when (currentMapDetail) {
            MapLayer.TEMPERATURE -> selectCardView(binding.temperatureMapCardView, binding.temperatureText, null, 1)
            MapLayer.WIND -> selectCardView(binding.windMapCardView, binding.windText, null, 2)
            MapLayer.RAIN -> selectCardView(binding.rainMapCardView, binding.rainText, null, 3)
            else -> {}
        }
    }

    interface MapItemClickListener {
        fun onMapTypeItemClick(mapTypeItem: Int)
        fun onMapDetailItemClick(mapDetailItem: MapLayer)
    }
}