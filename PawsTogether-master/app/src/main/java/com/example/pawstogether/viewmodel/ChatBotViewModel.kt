package com.example.pawstogether.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawstogether.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatBotViewModel : ViewModel() {

    private val model = GenerativeModel(
        modelName = "gemini-2.5-flash", // ✅ Modelo compatible con generateContent
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    private val _messages = MutableStateFlow<List<Pair<String, Boolean>>>(emptyList())
    val messages: StateFlow<List<Pair<String, Boolean>>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun sendMessage(userMessage: String) {
        val text = userMessage.trim()
        if (text.isEmpty()) return

        _messages.update { it + (text to true) }

        viewModelScope.launch {
            try {
                _isLoading.value = true

                val prompt = """
                    Actúa como PawsBot, un asesor amable en el cuidado de mascotas. 
                    Responde breve, claro y con consejos prácticos.
                    Usuario: $text
                """.trimIndent()

                val response = model.generateContent(prompt)

                val reply = response.text ?: "Lo siento, no tengo una respuesta en este momento 🐾"
                _messages.update { it + (reply to false) }

            } catch (e: Exception) {
                _messages.update {
                    it + ("Error: ${e.localizedMessage}" to false)
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
}
