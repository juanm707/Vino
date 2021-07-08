package com.example.vino.network

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.vino.model.Vineyard
import com.squareup.moshi.Json

@Entity(tableName = "User")
class VineyardManagerUser(
    val firstName: String,
    val lastName: String,
    val company: String,
    @PrimaryKey val userId: Int,
    val joinDate: String,
    val active: Boolean,
    val phone: String,
    val email: String
)
