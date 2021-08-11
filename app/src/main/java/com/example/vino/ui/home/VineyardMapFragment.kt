package com.example.vino.ui.home

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.net.MalformedURLException
import java.net.URL
import java.util.*

const val MAP_TYPE = "mapType"
const val MAP_DETAIL = "mapDetail"

class VineyardMapFragment : Fragment(), GoogleMap.OnPolygonClickListener, MapLayersBottomSheetDialogFragment.MapItemClickListener {

    private val vineyardMapFragmentViewModel: VineyardMapFragmentViewModel by viewModels {
        VineyardMapFragmentViewModelFactory((requireActivity().application as VinoApplication).repository)
    }

    private var _binding: FragmentVineyardMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap

    private var vineyardId: Int = 0

    private var viewTemperature: Boolean = false

    private var clickedBlock: String? = null

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private var currentMapType = MAP_TYPE_HYBRID
    private var currentMapDetail = MapLayer.NONE
    private var currentTileOverlay: TileOverlay? = null

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

        setUpBottomSheet()

        if (viewTemperature) {
            // if displaying weather
            currentMapType = MAP_TYPE_HYBRID
            currentMapDetail = MapLayer.TEMPERATURE
            currentTileOverlay = map.addTileOverlay(
                TileOverlayOptions()
                    .tileProvider(MapTileProvider("temp").getProvider())
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
            addPolygonBlocks(googleMap)
        })

        googleMap.mapType = currentMapType
        googleMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMyLocationButtonEnabled = true
        }
        googleMap.setOnPolygonClickListener(this)
        enableMyLocation()
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

    private fun enableMyLocation() {
        if (!::map.isInitialized) return
        if (ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        }
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
            setTemperatureScale()
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

    private fun setUpBottomSheet() {
        binding.mapLayerSelectFab.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(MAP_TYPE, currentMapType)
            bundle.putInt(MAP_DETAIL, currentMapDetail.ordinal)

            val mapLayerBottomSheetFragment = MapLayersBottomSheetDialogFragment.newInstance(bundle)
            mapLayerBottomSheetFragment.setListener(this)
            mapLayerBottomSheetFragment.show(parentFragmentManager, "MapLayerBottomSheetFragment")
        }
    }

    override fun onMapTypeItemClick(mapTypeItem: Int) {
        if (currentMapType != mapTypeItem) {
            when (mapTypeItem) {
                MAP_TYPE_NORMAL -> map.mapType = MAP_TYPE_NORMAL
                MAP_TYPE_HYBRID -> map.mapType = MAP_TYPE_HYBRID
                MAP_TYPE_TERRAIN -> map.mapType = MAP_TYPE_TERRAIN
            }
            currentMapType = mapTypeItem
        }
    }

    override fun onMapDetailItemClick(mapDetailItem: MapLayer) {
        if (currentMapDetail != mapDetailItem) {
            when(mapDetailItem) {
                MapLayer.TEMPERATURE -> {
                    setMapTileProvider("temp")
                    binding.temperatureScale.visibility = View.VISIBLE
                    setTemperatureScale()
                }
                MapLayer.WIND -> {
                    setMapTileProvider("wind")
                    binding.temperatureScale.visibility = View.GONE
                }
                MapLayer.RAIN -> {
                    setMapTileProvider("precipitation")
                    binding.temperatureScale.visibility = View.GONE
                }
                else -> {}
            }
            currentMapDetail = mapDetailItem
        } else {
            // if they are the same remove it
            if (currentMapDetail == MapLayer.TEMPERATURE)
                binding.temperatureScale.visibility = View.GONE

            currentMapDetail = MapLayer.NONE
            currentTileOverlay?.remove()
        }
    }

    private fun setMapTileProvider(type: String) {
        currentTileOverlay?.remove() // remove old one
        val newTileProvider = MapTileProvider(type)
        currentTileOverlay = map.addTileOverlay(
            TileOverlayOptions()
                .tileProvider(newTileProvider.getProvider())
        )
    }

    private fun setTemperatureScale() {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            requireContext().resources.getIntArray(R.array.temperature_scale)
        )
        binding.temperatureScaleImage.setImageDrawable(gradientDrawable)
    }
}
