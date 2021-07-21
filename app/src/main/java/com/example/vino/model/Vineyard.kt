package com.example.vino.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.lang.StringBuilder

@Entity(tableName = "Vineyard")
class Vineyard(
    @PrimaryKey val vineyardId: Int,
    val name: String,
    val imageUrl: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val city: String,
    val state: String,
)
