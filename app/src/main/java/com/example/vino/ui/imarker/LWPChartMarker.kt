package com.example.vino.ui.imarker

import android.content.Context
import android.widget.TextView
import com.example.vino.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*

class LWPChartMarker(context: Context?, layoutResource: Int) : MarkerView(context, layoutResource) {

    private var textView: TextView = findViewById(R.id.lwp_entry_data)
    private var mOffset: MPPointF? = null

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if (e != null) {
            val sdf = SimpleDateFormat("M/d", Locale.US)
            val date = sdf.format(Date(e.x.toLong()))
            textView.text = "$date | ${e.y}"
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        if (mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
        }
        return mOffset as MPPointF
    }
}