package com.example.vino.network

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.vino.model.Vineyard
import com.squareup.moshi.Json

class VineyardManagerUser(
    val userName: String,
    val userID: Int,
    val joinDate: String,
    val active: Boolean,
    val head: Head,
    val todoAmount: Int,
    val vineyards: List<Vineyard>
)

class Head(
    val name: String,
    val phone: String,
    val email: List<String>
)

@Entity(tableName = "Todo")
class Todo(
    @PrimaryKey val id: Int,
    val job: String,
    val description: String,
    @Json(name = "due") var dueDate: String,
    var completed: Boolean
)