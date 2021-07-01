package com.example.vino.model

import com.example.vino.network.Coordinates

class Vineyard(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val temperature: Int,
    val humidity: Int,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)