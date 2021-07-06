package com.example.vino.ui.formatter

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class LWPXAxisValueFormatter : ValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        return try {
            val sdf = SimpleDateFormat("M/d", Locale.US)
            sdf.format(Date(value.toLong()))
        } catch (e: Exception) {
            return value.toString()
        }
    }
}