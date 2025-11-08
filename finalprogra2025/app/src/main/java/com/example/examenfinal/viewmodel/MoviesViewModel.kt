// FILE: app/src/main/java/com/example/examenfinal/viewmodel/MoviesViewModel.kt
package com.example.examenfinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examenfinal.data.model.Movie
import com.example.examenfinal.data.model.Rental
import com.example.examenfinal.data.repository.MoviesRepository
import com.example.examenfinal.data.repository.RentalsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MoviesViewModel(
    private val moviesRepo: MoviesRepository = MoviesRepository(),
    private val rentalsRepo: RentalsRepository = RentalsRepository()
) : ViewModel() {

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun loadMovies() {
        viewModelScope.launch {
            _movies.value = moviesRepo.getMovies()
        }
    }

    fun rentMovie(
        movie: Movie,
        userId: String,
        userName: String,
        startDate: String,
        endDate: String
    ) {
        viewModelScope.launch {
            try {
                val rental = Rental(
                    userId = userId,
                    userName = userName,
                    movieId = movie.id,
                    movieTitle = movie.title,
                    startDate = startDate,
                    endDate = endDate
                )
                rentalsRepo.createRental(rental)
                _message.value = "Renta registrada"
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
