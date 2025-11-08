// FILE: app/src/main/java/com/example/examenfinal/ExamenFinalApp.kt
package com.example.examenfinal

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.examenfinal.navigation.AppNavGraph

@Composable
fun ExamenFinalApp() {
    val navController = rememberNavController()
    AppNavGraph(navController = navController)
}
