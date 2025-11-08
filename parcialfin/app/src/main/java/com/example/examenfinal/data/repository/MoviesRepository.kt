// FILE: app/src/main/java/com/example/examenfinal/data/repository/MoviesRepository.kt
package com.example.examenfinal.data.repository

import com.example.examenfinal.data.model.Movie
import com.example.examenfinal.data.remote.FirestoreDataSource

class MoviesRepository(
    private val firestore: FirestoreDataSource = FirestoreDataSource()
) {

    suspend fun getMovies(): List<Movie> {
        firestore.seedMoviesIfEmpty()
        return firestore.getMovies()
    }
}
