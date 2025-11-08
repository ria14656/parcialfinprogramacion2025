// FILE: app/src/main/java/com/example/examenfinal/data/model/User.kt
package com.example.examenfinal.data.model

data class User(
    val uid: String = "",
    val fullName: String = "",
    val age: Int? = null,
    val cardNumber: String = "",
    val photoUrl: String = "",
    val email: String = "",
    val role: String = "USER"
)
