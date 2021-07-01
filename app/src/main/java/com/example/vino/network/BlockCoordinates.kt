package com.example.vino.network

class BlockCoordinates( // for api response
    val id: Int,
    val vineyardId: Int,
    val name: String,
    val coordinates: List<Coordinates>
)


class Coordinates(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
)