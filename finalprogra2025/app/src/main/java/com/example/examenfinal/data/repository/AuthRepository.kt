// FILE: app/src/main/java/com/example/examenfinal/data/repository/AuthRepository.kt
package com.example.examenfinal.data.repository

import com.example.examenfinal.data.model.User
import com.example.examenfinal.data.remote.FirebaseAuthDataSource
import com.example.examenfinal.data.remote.FirestoreDataSource
import com.example.examenfinal.utils.Constants

class AuthRepository(
    private val auth: FirebaseAuthDataSource = FirebaseAuthDataSource(),
    private val firestore: FirestoreDataSource = FirestoreDataSource()
) {


    suspend fun register(email: String, password: String): Result<Unit> = try {
        val uid = auth.register(email, password)

        val role = if (email == Constants.DEFAULT_ADMIN_EMAIL) {
            Constants.ROLE_ADMIN
        } else {
            Constants.ROLE_USER
        }

        val user = User(
            uid = uid,
            email = email,
            role = role
        )

        firestore.saveUser(user)

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Login simple
    suspend fun login(email: String, password: String): Result<Unit> = try {
        auth.login(email, password)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun logout() {
        auth.logout()
    }

    fun currentUserId(): String? = auth.currentUserId()
}
