package com.example.peliculas.ui

sealed class Route(val r: String){
    data object Auth: Route("auth")
    data object Home: Route("home")
    data object Profile: Route("profile")
    data object Movies: Route("movies")
    data object Admin: Route("admin")
}
