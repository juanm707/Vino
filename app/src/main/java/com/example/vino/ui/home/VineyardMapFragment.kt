package com.example.vino.ui.home

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.vino.R
import com.example.vino.VinoApplication
import com.example.vino.databinding.FragmentVineyardMapBinding
import com.example.vino.model.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.model.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.net.MalformedURLException
import java.net.URL
import java.util.*


class VineyardMapFragment : Fragment(), GoogleMap.OnPolygonClickListener {

    private val vineyardMapFragmentViewModel: VineyardMapFragmentViewModel by viewModels {
        VineyardMapFragmentViewModelFactory((requireActivity().application as VinoApplication).repository)
    }

    private var _binding: FragmentVineyardMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap
    private var vineyardId: Int = 0
    private var viewTemperature: Boolean = false

    private var clickedBlock: String? = null

    var tileProvider: TileProvider = object : UrlTileProvider(256, 256) {
        override fun getTileUrl(x: Int, y: Int, zoom: Int): URL? {

            /* Define the URL pattern for the tile images */
            val url3 = String.format(Locale.US, "https://tile.openweathermap.org/map/temp_new/%d/%d/%d.png?appid=ee79ad0d5b1a83ff07fce20435019619", zoom, x, y)
            try {
                return URL(url3)
            } catch (e: MalformedURLException) {
                throw AssertionError(e)
            }
        }
    }

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        map = googleMap

        if (viewTemperature) {
            // if displaying weather
            val tileOverlay = map.addTileOverlay(
                TileOverlayOptions()
                    .tileProvider(tileProvider)
            )
        }

        vineyardMapFragmentViewModel.vineyard.observe(viewLifecycleOwner, { vineyard ->

            val vineyardLocation = LatLng(vineyard.latitude, vineyard.longitude)
            googleMap.addMarker(
                MarkerOptions()
                    .position(vineyardLocation)
                    .title(vineyard.name)
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.grape_logo)) // must use svg png etc not xml
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(vineyardLocation))
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(18f))
            googleMap.mapType = MAP_TYPE_HYBRID
            googleMap.uiSettings.apply {
                isZoomControlsEnabled = true
                isCompassEnabled = true
            }
            googleMap.setOnPolygonClickListener(this)

            addPolygonBlocks(googleMap)
        })
    }

    /*
     * Check that the tile server supports the requested x, y and zoom.
     * Complete this stub according to the tile range you support.
     * If you support a limited range of tiles at different zoom levels, then you
     * need to define the supported x, y range at each zoom level.
     */
    private fun checkTileExists(x: Int, y: Int, zoom: Int): Boolean {
        val minZoom = 12
        val maxZoom = 16
        return zoom in minZoom..maxZoom
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            vineyardId = it.getInt("vineyardId", 0)
            viewTemperature = it.getBoolean("viewTemperature")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVineyardMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get the temperature scale
        if (viewTemperature) {
            val gradientDrawable: GradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                requireContext().resources.getIntArray(R.array.temperature_scale)
            )
            binding.temperatureScaleImage.setImageDrawable(gradientDrawable)
        } else
            binding.temperatureScale.visibility = View.GONE

        vineyardMapFragmentViewModel.setVineyard(vineyardId)

        vineyardMapFragmentViewModel.vineyard.observe(viewLifecycleOwner, { vineyard ->
            binding.vineyardNameMapTitle.text = vineyard.name
            vineyardMapFragmentViewModel.refreshBlocks(vineyard.vineyardId) // to display the blocks on map
        })

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        setUpBanner()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPolygonClick(polygon: Polygon) {
        clickedBlock = polygon.tag as String?
        centerPolygon(polygon)
        if (!binding.banner.isShown)
            binding.banner.show()
    }

    private fun setUpBanner() {
        binding.banner.setLeftButtonListener {
            binding.banner.dismiss()
        }

        binding.banner.setRightButtonListener {
            binding.banner.dismiss()

            MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered)
                .setNegativeButton("Dismiss") { dialog, which ->
                    // Respond to negative button press
                }
                .setView(getBlockInfoLayout(layoutInflater))
                .show()
        }
    }

    private fun centerPolygon(polygon: Polygon) {
        val latLngBounds = LatLngBounds.Builder()
        polygon.points.forEach { coordinate ->
            latLngBounds.include(coordinate)
        }
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 40))
    }

    private fun addPolygonBlocks(googleMap: GoogleMap) {
        vineyardMapFragmentViewModel.blocks.observe(viewLifecycleOwner, { blocks ->
            blocks.forEach { block ->
                addPolygonBlock(googleMap, block)
            }
        })
    }

    private fun addPolygonBlock(googleMap: GoogleMap, parentBlock: BlockWithCoordinates) {
        // Add polygons to indicate areas on the map.
        val polygon = googleMap.addPolygon(getPolygon(parentBlock.coordinates))
        // Store a data object with the polygon, used here to indicate an arbitrary type.
        polygon.tag = parentBlock.block.name
    }

    private fun getPolygon(coordinates: List<Coordinate>): PolygonOptions {
        val newPolygon = PolygonOptions()
            .clickable(true)
            .strokeColor(ContextCompat.getColor(requireContext(), R.color.block_stroke))
            .fillColor(ContextCompat.getColor(requireContext(), R.color.block_fill))

        coordinates.forEach { coordinate ->
            newPolygon.add(LatLng(coordinate.latitude, coordinate.longitude))
        }

        return newPolygon
    }

    private fun getBlockInfoLayout(inflater: LayoutInflater): View {
        val selectedBlock = vineyardMapFragmentViewModel.getBlockForName(clickedBlock)
        val dialogLayout = inflater.inflate(R.layout.block_info_alert_dialog, null)
        if (selectedBlock != null) {
            dialogLayout.findViewById<TextView>(R.id.name_actual).text = selectedBlock.name
            dialogLayout.findViewById<TextView>(R.id.variety_actual).text = selectedBlock.variety
            dialogLayout.findViewById<TextView>(R.id.acres_actual).text = "${selectedBlock.acres} ac."
            dialogLayout.findViewById<TextView>(R.id.vines_actual).text = "${selectedBlock.vines}"
            dialogLayout.findViewById<TextView>(R.id.rootstock_actual).text = selectedBlock.rootstock
            dialogLayout.findViewById<TextView>(R.id.clone_actual).text = selectedBlock.clone
            dialogLayout.findViewById<TextView>(R.id.year_actual).text = "${selectedBlock.yearPlanted}"
            dialogLayout.findViewById<TextView>(R.id.row_space_actual).text = "${selectedBlock.rowSpacing} ft."
            dialogLayout.findViewById<TextView>(R.id.plant_space_actual).text = "${selectedBlock.vineSpacing} ft."
        }
        return dialogLayout
    }
}
