package com.example.vino.vinodao

import androidx.room.Entity


@Entity(tableName = "Coordinates", primaryKeys = ["vineyardId", "id"])
class Coordinates(
    val id: Int,
    val vineyardId: Int,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
)