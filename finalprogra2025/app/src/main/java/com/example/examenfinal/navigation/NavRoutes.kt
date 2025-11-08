
package com.example.examenfinal.navigation

sealed class NavRoute(val route: String) {
    object Login : NavRoute("login")
    object Register : NavRoute("register")
    object Home : NavRoute("home")
    object Profile : NavRoute("profile")
    object Movies : NavRoute("movies")
    object AdminReport : NavRoute("admin_report")
}
