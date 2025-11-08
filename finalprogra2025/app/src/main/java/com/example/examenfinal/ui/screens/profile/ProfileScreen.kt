// FILE: app/src/main/java/com/example/examenfinal/ui/screens/profile/ProfileScreen.kt
@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.examenfinal.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Perfil de usuario") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("nom: Nombre completo")
            Text("Edad")
            Text("Número de tarjeta de crédito")
            Text("Fotografía")
        }
    }
}
