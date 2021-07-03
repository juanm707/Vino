package com.example.vino.network

class BlockCoordinates( // for api response
    val id: Int,
    val vineyardId: Int,
    val name: String,
    val coordinates: List<Coordinate>,
    val variety: String,
    val acres: Float,
    val vines: Int,
    val rootstock: String,
    val clone: String,
    val yearPlanted: Int,
    val rowSpacing: Float,
    val vineSpacing: Float
)


class Coordinate(
    val id: Int,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
)