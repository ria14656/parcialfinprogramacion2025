package com.example.pawstogether.network

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.Call

data class ContentRequest(val contents: List<Content>)
data class Content(val parts: List<Part>)
data class Part(val text: String)

data class GeminiResponse(val candidates: List<Candidate>?)
data class Candidate(val content: Content?)

interface GeminiApiService {
    @POST("v1/models/gemini-pro:generateContent")
    fun generateContent(
        @Query("key") apiKey: String,
        @Body request: ContentRequest
    ): Call<GeminiResponse>
}