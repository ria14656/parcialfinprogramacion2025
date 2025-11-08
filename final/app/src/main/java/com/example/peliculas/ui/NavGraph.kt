package com.example.peliculas.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavGraph(nav: NavHostController) {
    NavHost(navController = nav, startDestination = Route.Auth.r) {
        composable(Route.Auth.r) {
            AuthScreen(onDone = {
                nav.navigate(Route.Home.r) { popUpTo(Route.Auth.r) { inclusive = true } }
            })
        }
        composable(Route.Home.r)   { HomeMenu(
            goProfile = { nav.navigate(Route.Profile.r) },
            goMovies  = { nav.navigate(Route.Movies.r) },
            goAdmin   = { nav.navigate(Route.Admin.r) }
        ) }
        composable(Route.Profile.r){ ProfileScreen() }
        composable(Route.Movies.r) { MoviesScreen() }
        composable(Route.Admin.r)  { AdminScreen() }
    }
}
