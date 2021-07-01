package com.example.vino.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "Block")
class Block(
    @PrimaryKey val id: Int,
    val vineyardId: Int,
    val name: String
)
