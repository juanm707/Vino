package com.example.vino.ui.adapter

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.vino.R
import com.example.vino.network.VinoApiStatus

@BindingAdapter("vinoApiStatus")
fun bindStatus(statusImageView: ImageView, status: VinoApiStatus) {
    when (status) {
        VinoApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_baseline_cloud_off_24)
        }
        VinoApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}
