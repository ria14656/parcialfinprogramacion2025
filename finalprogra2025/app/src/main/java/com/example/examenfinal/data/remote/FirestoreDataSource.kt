// FILE: app/src/main/java/com/example/examenfinal/data/remote/FirestoreDataSource.kt
package com.example.examenfinal.data.remote

import com.example.examenfinal.data.model.Movie
import com.example.examenfinal.data.model.Rental
import com.example.examenfinal.data.model.User
import com.example.examenfinal.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreDataSource(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // USERS
    suspend fun saveUser(user: User) {
        db.collection(Constants.USERS_COLLECTION)
            .document(user.uid)
            .set(user)
            .await()
    }

    suspend fun getUser(uid: String): User? {
        val doc = db.collection(Constants.USERS_COLLECTION)
            .document(uid)
            .get()
            .await()
        return doc.toObject(User::class.java)
    }

    // MOVIES
    suspend fun getMovies(): List<Movie> {
        val snapshot = db.collection(Constants.MOVIES_COLLECTION)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(Movie::class.java)?.copy(id = it.id) }
    }

    suspend fun seedMoviesIfEmpty() {
        val snapshot = db.collection(Constants.MOVIES_COLLECTION)
            .limit(1)
            .get()
            .await()
        if (!snapshot.isEmpty) return

        val movies = listOf(
            Movie(title = "Inception", description = "Ciencia ficción"),
            Movie(title = "The Dark Knight", description = "Acción"),
            Movie(title = "Interstellar", description = "Espacio"),
            Movie(title = "Toy Story", description = "Animada")
        )

        val col = db.collection(Constants.MOVIES_COLLECTION)
        movies.forEach { movie ->
            col.add(movie).await()
        }
    }

    // RENTALS
    suspend fun createRental(rental: Rental) {
        val docRef = db.collection(Constants.RENTALS_COLLECTION).document()
        docRef.set(rental.copy(id = docRef.id)).await()
    }

    suspend fun getAllRentals(): List<Rental> {
        val snapshot = db.collection(Constants.RENTALS_COLLECTION)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(Rental::class.java)?.copy(id = it.id) }
    }
}
