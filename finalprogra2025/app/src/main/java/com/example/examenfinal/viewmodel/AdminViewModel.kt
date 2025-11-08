// FILE: app/src/main/java/com/example/examenfinal/viewmodel/AdminViewModel.kt
package com.example.examenfinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examenfinal.data.model.Rental
import com.example.examenfinal.data.repository.RentalsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminViewModel(
    private val repo: RentalsRepository = RentalsRepository()
) : ViewModel() {

    private val _rentals = MutableStateFlow<List<Rental>>(emptyList())
    val rentals: StateFlow<List<Rental>> = _rentals

    fun loadRentals() {
        viewModelScope.launch {
            _rentals.value = repo.getAllRentals()
        }
    }
}
