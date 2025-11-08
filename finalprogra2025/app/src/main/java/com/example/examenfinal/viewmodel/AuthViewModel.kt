// FILE: app/src/main/java/com/example/examenfinal/viewmodel/AuthViewModel.kt
package com.example.examenfinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examenfinal.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun register(email: String, password: String, onSuccess: () -> Unit) {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            val result = repo.register(email, password)
            _loading.value = false

            result
                .onSuccess { onSuccess() }
                .onFailure { e -> _error.value = e.message }
        }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            val result = repo.login(email, password)
            _loading.value = false

            result
                .onSuccess { onSuccess() }
                .onFailure { e -> _error.value = e.message }
        }
    }

    fun logout() = repo.logout()

    fun currentUserId(): String? = repo.currentUserId()
}
