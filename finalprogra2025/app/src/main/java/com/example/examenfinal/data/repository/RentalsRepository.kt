// FILE: app/src/main/java/com/example/examenfinal/data/repository/RentalsRepository.kt
package com.example.examenfinal.data.repository

import com.example.examenfinal.data.model.Rental
import com.example.examenfinal.data.remote.FirestoreDataSource

class RentalsRepository(
    private val firestore: FirestoreDataSource = FirestoreDataSource()
) {

    suspend fun createRental(rental: Rental) {
        firestore.createRental(rental)
    }

    suspend fun getAllRentals(): List<Rental> {
        return firestore.getAllRentals()
    }
}
