package com.example.clase7.models

data class Rental(
    val id: String = "",
    val movieId: String = "",
    val movieTitle: String = "",
    val userId: String = "",
    val userName: String = "",
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)
