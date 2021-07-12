package com.example.vino.ui.home

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.vino.R
import com.example.vino.VinoApplication
import com.example.vino.databinding.FragmentVineyardMapBinding
import com.example.vino.model.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID
import com.google.android.gms.maps.model.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private var clickedBlock: String? = null

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        map = googleMap

//        var tileProvider: TileProvider = object : UrlTileProvider(256, 256) {
//            override fun getTileUrl(x: Int, y: Int, zoom: Int): URL? {
//
//                /* Define the URL pattern for the tile images */
//                val url = "http://my.image.server/images/zoom/$x/$y.png"
//                val url2 = "https://tile.openweathermap.org/map/temperature/$zoom/$x/$y.png?appid=ee79ad0d5b1a83ff07fce20435019619"
//                val url3 = String.format(Locale.US, "https://tile.openweathermap.org/map/temperature/%d/%d/%d.png?appid=ee79ad0d5b1a83ff07fce20435019619", zoom, x, y)
//                try {
//                    return URL(url3)
//                } catch (e: MalformedURLException) {
//                    throw AssertionError(e)
//                }
//            }
//        }
//
//        val tileOverlay = map.addTileOverlay(
//            TileOverlayOptions()
//                .tileProvider(tileProvider)
//        )

        vineyardMapFragmentViewModel.vineyard.observe(viewLifecycleOwner, { vineyard ->

            val vineyardLocation = LatLng(vineyard.latitude, vineyard.longitude)
            googleMap.addMarker(
                MarkerOptions()
                    .position(vineyardLocation)
                    .title(vineyard.name)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.grape_logo)) // must svg png etc not xml
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
                .setMessage(getBlockInfo())
                .setNegativeButton("Dismiss") { dialog, which ->
                    // Respond to negative button press
                }
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

    private fun getBlockInfo(): String {
        val selectedBlock = vineyardMapFragmentViewModel.getBlockForName(clickedBlock)
        return if (selectedBlock == null)
            "No block info available..."
        else {
            "Name: ${selectedBlock.name}" + '\n' +
                    "Variety: ${selectedBlock.variety}" + '\n' +
                    "Acres: ${selectedBlock.acres}" + '\n' +
                    "Vines: ${selectedBlock.vines}" + '\n' +
                    "Rootstock: ${selectedBlock.rootstock}" + '\n' +
                    "Clone: ${selectedBlock.clone}" + '\n' +
                    "Year Planted: ${selectedBlock.yearPlanted}" + '\n' +
                    "Row Spacing: ${selectedBlock.rowSpacing} ft." + '\n' +
                    "Vine Spacing: ${selectedBlock.vineSpacing} ft."
        }
    }
}
