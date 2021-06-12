package com.example.vino.network

import com.squareup.moshi.Json

data class VineyardManagerUser(
    val userName: String,
    val userID: Int,
    val joinDate: String,
    val active: Boolean,
    val head: Head,
    val todoAmount: Int,
    val vineyards: List<Vineyard>
)

data class Head(
    val name: String,
    val phone: String,
    val email: List<String>
)

data class Vineyard(
    val name: String,
    val imageUrl: String
)

data class Todo(
    val id: Int,
    val job: String,
    val description: String,
    @Json(name = "due") val dueDate: String,
    val completed: Boolean
)