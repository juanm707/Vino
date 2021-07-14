package com.example.vino.ui

import com.facebook.shimmer.Shimmer

class ImageShimmer {
    val shimmer = Shimmer.AlphaHighlightBuilder()// The attributes for a ShimmerDrawable is set by this builder
        .setDuration(1200) // how long the shimmering animation takes to do one full sweep
        .setBaseAlpha(0.8f) //the alpha of the underlying children
        .setHighlightAlpha(0.9f) // the shimmer alpha amount
        .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
        .setAutoStart(true)
        .build()
}