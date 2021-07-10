package com.example.vino.ui.home

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.vino.R
import com.example.vino.VinoApplication
import com.example.vino.databinding.FragmentLeafWaterPotentialBinding
import com.example.vino.ui.formatter.LWPXAxisValueFormatter
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class LeafWaterPotentialFragment : Fragment() {

    private val leafWaterPotentialFragmentViewModel: LeafWaterPotentialFragmentViewModel by viewModels {
        LeafWaterPotentialFragmentViewModelFactory((requireActivity().application as VinoApplication).repository)
    }

    private var vineyardId: Int = 0

    private var _binding: FragmentLeafWaterPotentialBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            vineyardId = it.getInt("vineyardId", 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLeafWaterPotentialBinding.inflate(inflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        leafWaterPotentialFragmentViewModel.refreshLWPReadings(vineyardId)

        val chart = binding.chart

        setUpChart(chart)
        setChartData(chart)

        leafWaterPotentialFragmentViewModel.vineyardName.observe(viewLifecycleOwner, { vineyardName ->
            binding.cameraButton.setOnClickListener {
                shareChartBitmap(chart.chartBitmap, vineyardName)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setUpChart(chart: LineChart) {

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = LWPXAxisValueFormatter()
        xAxis.setDrawGridLines(false)
        xAxis.axisLineWidth = 3F
        xAxis.axisLineColor = ContextCompat.getColor(requireContext(), R.color.black)
        xAxis.textSize = 12F

        val yAxis = chart.axisLeft
        yAxis.axisLineWidth = 3F
        yAxis.axisLineColor = ContextCompat.getColor(requireContext(), R.color.black)
        yAxis.textSize = 12F

        val ll = LimitLine(15F, "High Plant Stress")
        ll.lineColor = ContextCompat.getColor(requireContext(), R.color.design_default_color_error)
        ll.lineWidth = 2F
        ll.textColor = ContextCompat.getColor(requireContext(), R.color.black)
        ll.textSize = 12F

        yAxis.addLimitLine(ll)
        yAxis.setDrawLimitLinesBehindData(true)

        chart.axisRight.isEnabled = false
        chart.setDrawGridBackground(false)
        chart.description.isEnabled = false
        chart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        chart.animateXY(1500, 1500, Easing.EaseInOutBack)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setChartData(chart: LineChart) {

        val targetEntries = mutableListOf(
            Entry(1609524000000F, 10F),
            Entry(1610128800000F, 10F),
            Entry(1610733600000F, 10F),
            Entry(1611338400000F, 11F),
            Entry(1611943200000F, 12F),
            Entry(1612634400000F, 12F),
            Entry(1613239200000F, 13F),
            Entry(1613844000000F, 12F),
            Entry(1614448800000F, 12F),
            Entry(1614880800000F, 11F),
            Entry(1615485600000F, 10F),
            Entry(1616090400000F, 10F),
            Entry(1616695200000F, 10F),
            Entry(1617300000000F, 9F),
            Entry(1617904800000F, 9F)
        )

        val targetName = "Target"
        val targetDataset = LineDataSet(targetEntries, targetName)
        targetDataset.enableDashedLine(10F,10F,0F)

        val color2 = ContextCompat.getColor(requireContext(), R.color.target_line)

        setDataOptions(targetDataset, color2)

        val dataSets: MutableList<ILineDataSet> = ArrayList()
        dataSets.add(targetDataset)

        leafWaterPotentialFragmentViewModel.lwpReadings.observe(viewLifecycleOwner, { lwpReadingMap ->
            lwpReadingMap.forEach { (blockName, blockReadings) ->
                if (blockReadings.isNotEmpty()) {
                    val entries = mutableListOf<Entry>()
                    blockReadings.forEach { reading ->
                        entries.add(Entry(reading.timestamp.toFloat(), reading.barPressureData))
                    }
                    val blockDataset = LineDataSet(entries, blockName)
                    setDataOptions(blockDataset, getRandomColor())
                    dataSets.add(blockDataset)
                }
            }

            if (lwpReadingMap.isNotEmpty()) {
                addDataToChart(chart, dataSets)
                chart.notifyDataSetChanged()
                chart.invalidate()
            }
        })
    }

    private fun setDataOptions(dataSet: LineDataSet, color: Int) {
        dataSet.setDrawValues(false)
        dataSet.color = color
        dataSet.lineWidth = 3f
        dataSet.isHighlightEnabled = true
        dataSet.setCircleColor(color)
        dataSet.circleRadius = 5f
        dataSet.setDrawCircleHole(false)
    }

    private fun addDataToChart(chart: LineChart, dataSets: MutableList<ILineDataSet>) {
        val lineData = LineData(dataSets)
        chart.data = lineData
        chart.setExtraOffsets(10F, 0F, 10F, 10F)
    }

    private fun getRandomColor(): Int {
        val colors = requireContext().resources.getIntArray(R.array.chart_colors)
        return colors[Random().nextInt(colors.size)]
    }

    private fun shareChartBitmap(bitmap: Bitmap, vineyardName: String) {
        // get cache directory
        val cachePath = File(requireContext().externalCacheDir, "my_images/")
        cachePath.mkdirs()

        //create png file
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val file = File(cachePath, "${vineyardName}_Leaf_Water_Potential_Chart_$month-$day.png")
        val fileOutputStream: FileOutputStream

        try {
            fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // get file uri
        val chartImageFileUri: Uri = FileProvider.getUriForFile(
            requireContext().applicationContext,
            requireContext().applicationContext.packageName + ".fileprovider",
            file
        )

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, chartImageFileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            setDataAndType(chartImageFileUri, "image/png") // setting removes the permission error
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
}
