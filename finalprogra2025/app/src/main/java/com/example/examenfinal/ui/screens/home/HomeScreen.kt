// FILE: app/src/main/java/com/example/examenfinal/ui/screens/home/HomeScreen.kt
@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.examenfinal.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.examenfinal.navigation.NavRoute

@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Menú principal") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                onClick = { navController.navigate(NavRoute.Profile.route) }
            ) {
                Text("Perfil de usuario")
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                onClick = { navController.navigate(NavRoute.Movies.route) }
            ) {
                Text("Lista de películas")
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                onClick = { navController.navigate(NavRoute.AdminReport.route) }
            ) {
                Text("Informe de rentas (ADMIN)")
            }
        }
    }
}
