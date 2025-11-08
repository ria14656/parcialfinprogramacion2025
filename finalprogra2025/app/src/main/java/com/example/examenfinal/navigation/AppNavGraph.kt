// FILE: app/src/main/java/com/example/examenfinal/navigation/AppNavGraph.kt
package com.example.examenfinal.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.examenfinal.ui.screens.admin.AdminReportScreen
import com.example.examenfinal.ui.screens.auth.LoginScreen
import com.example.examenfinal.ui.screens.auth.RegisterScreen
import com.example.examenfinal.ui.screens.home.HomeScreen
import com.example.examenfinal.ui.screens.movies.MoviesListScreen
import com.example.examenfinal.ui.screens.profile.ProfileScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoute.Login.route
    ) {
        composable(NavRoute.Login.route) {
            LoginScreen(navController)
        }
        composable(NavRoute.Register.route) {
            RegisterScreen(navController)
        }
        composable(NavRoute.Home.route) {
            HomeScreen(navController)
        }
        composable(NavRoute.Profile.route) {
            ProfileScreen()
        }
        composable(NavRoute.Movies.route) {
            MoviesListScreen()
        }
        composable(NavRoute.AdminReport.route) {
            AdminReportScreen()
        }
    }
}
