// FILE: app/src/main/java/com/example/examenfinal/data/repository/UserRepository.kt
package com.example.examenfinal.data.repository

import com.example.examenfinal.data.model.User
import com.example.examenfinal.data.remote.FirestoreDataSource

class UserRepository(
    private val firestore: FirestoreDataSource = FirestoreDataSource()
) {

    suspend fun getUser(uid: String): User? = firestore.getUser(uid)

    suspend fun saveUser(user: User) {
        firestore.saveUser(user)
    }
}
