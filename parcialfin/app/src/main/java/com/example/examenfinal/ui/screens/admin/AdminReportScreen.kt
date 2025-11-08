// FILE: app/src/main/java/com/example/examenfinal/ui/screens/admin/AdminReportScreen.kt
@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.examenfinal.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AdminReportScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Informe de rentas") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Aquí el administrador verá:")
            Text("- Película rentada")
            Text("- Usuario que rentó")
            Text("- Fecha de inicio y fin")
        }
    }
}
