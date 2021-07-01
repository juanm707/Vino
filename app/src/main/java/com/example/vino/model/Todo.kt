package com.example.vino.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "Todo")
class Todo(
    @PrimaryKey val id: Int,
    val job: String,
    val description: String,
    @Json(name = "due") var dueDate: String,
    var completed: Boolean
)