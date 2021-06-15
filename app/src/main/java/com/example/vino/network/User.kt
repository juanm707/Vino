package com.example.vino.network

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

class Vineyard(
    val name: String,
    val imageUrl: String
)

class Todo(
    val id: Int,
    val job: String,
    val description: String,
    @Json(name = "due") var dueDate: String,
    var completed: Boolean
)