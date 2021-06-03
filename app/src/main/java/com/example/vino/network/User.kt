package com.example.vino.network

data class VineyardManagerUser(
    val userName: String,
    val userID: Int,
    val joinDate: String,
    val active: Boolean,
    val head: Head,
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
