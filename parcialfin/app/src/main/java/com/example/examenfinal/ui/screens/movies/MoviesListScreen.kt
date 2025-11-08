// FILE: app/src/main/java/com/example/examenfinal/ui/screens/movies/MoviesListScreen.kt
@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.examenfinal.ui.screens.movies

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MoviesListScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Películas disponibles") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Aquí mostraremos la lista fija de películas desde Firestore.")
            Spacer(Modifier.height(16.dp))
            Button(onClick = { /* aquí luego llamamos a Rentar */ }) {
                Text("Rentar (demo)")
            }
        }
    }
}
