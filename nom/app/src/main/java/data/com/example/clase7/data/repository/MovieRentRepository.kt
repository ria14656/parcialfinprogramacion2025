package com.example.clase7.data.repository

import com.example.clase7.models.Movie
import com.example.clase7.models.Rental
import com.example.clase7.models.UserProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class MovieRentRepository {
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    // ---------- PERFIL ----------
    suspend fun getMyProfile(): UserProfile? {
        val uid = auth.currentUser?.uid ?: return null
        val snap = db.collection("users").document(uid).get().await()
        val p = snap.toObject(UserProfile::class.java)
        return p?.copy(uid = uid) ?: UserProfile(uid = uid)
    }

    suspend fun saveMyProfile(p: UserProfile) {
        require(p.uid.isNotBlank()) { "uid vacío" }
        db.collection("users").document(p.uid).set(p).await()
    }

    // ---------- PELÍCULAS ----------
    suspend fun seedMoviesIfEmpty() {
        val col = db.collection("movies")
        if (col.limit(1).get().await().isEmpty) {
            val list = listOf(
                Movie(title = "Inception", year = 2010,
                    posterUrl = "https://image.tmdb.org/t/p/w500/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg"),
                Movie(title = "Interstellar", year = 2014,
                    posterUrl = "https://image.tmdb.org/t/p/w500/rAiYTfKGqDCRIIqo664sY9XZIvQ.jpg"),
                Movie(title = "The Dark Knight", year = 2008,
                    posterUrl = "https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg")
            )
            list.forEach {
                val ref = col.document()
                ref.set(it.copy(id = ref.id)).await()
            }
        }
    }

    suspend fun listMovies(): List<Movie> =
        db.collection("movies").get().await().documents.mapNotNull {
            it.toObject(Movie::class.java)?.copy(id = it.id)
        }

    // ---------- RENTAS ----------
    suspend fun rentMovie(movie: Movie, start: Long, end: Long): Boolean {
        val me = getMyProfile() ?: return false
        val ref = db.collection("rentals").document()
        val rental = Rental(
            id = ref.id,
            movieId = movie.id,
            movieTitle = movie.title,
            userId = me.uid,
            userName = me.name,
            startDate = start,
            endDate = end
        )
        ref.set(rental).await()
        return true
    }

    suspend fun listAllRentals(): List<Rental> =
        db.collection("rentals").orderBy("createdAt").get().await()
            .documents.mapNotNull { it.toObject(Rental::class.java)?.copy(id = it.id) }
}
