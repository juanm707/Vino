package com.example.vino.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Vineyard")
class Vineyard(
    @PrimaryKey val vineyardId: Int,
    val name: String,
    val imageUrl: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val city: String,
    val state: String,
    val job: String,
    val sprayed: Boolean,
    val type: String?,
    val material: String?,
    val rei: Int?,
    val sprayOrderUrl: String?
)
