// domain/models/Report.kt
package com.example.clase7.models

data class Report(
    val id: String = "",
    val uid: String = "",
    val curso: String = "",
    val anio: Int = 0,
    val semestre: Int = 1,
    val fechaMillis: Long = 0L,
    val comentarios: String = "",
    val archivos: List<String> = emptyList()
)
