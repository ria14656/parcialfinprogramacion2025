package com.example.clase7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clase7.ui.screen.login.LoginScreen
import com.example.clase7.ui.screen.logsuccess.SuccessScreen
import com.example.clase7.ui.screen.register.RegisterScreen
import com.example.clase7.ui.screen.users.Users
import com.example.clase7.ui.screen.users.UsersFormScreen
import com.example.clase7.ui.theme.Clase7Theme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 1) Splash ANTES de super.onCreate
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // 2) Asegurar inicialización de Firebase una sola vez
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }

        // 3) Calcular una sola vez la ruta inicial
        val startDestination = if (Firebase.auth.currentUser != null) {
            "logSuccess"
        } else {
            "com/example/clase7/ui/login"
        }

        enableEdgeToEdge()
        setContent {
            Clase7Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        MainScreens(startDestination)
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreens(startDestination: String) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("com/example/clase7/ui/login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("logSuccess") { SuccessScreen(navController) }
        composable("users") { Users(navController) }
        composable("users_form") { UsersFormScreen(navController) }
    }
}
