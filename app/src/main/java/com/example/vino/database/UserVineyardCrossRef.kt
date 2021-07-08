package com.example.vino.database

import androidx.room.Entity

@Entity(primaryKeys = ["userId", "vineyardId"])
class UserVineyardCrossRef(
    val userId: Int,
    val vineyardId: Int
)