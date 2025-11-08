package com.example.clase7.models

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val age: Int = 0,
    val card: String = "",
    val photoUrl: String = "",
    val role: String = "user" // "user" o "admin"
)
