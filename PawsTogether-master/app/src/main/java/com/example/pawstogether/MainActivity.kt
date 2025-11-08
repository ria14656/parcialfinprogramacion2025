package com.example.pawstogether
import com.example.pawstogether.ui.theme.screens.ChatBotScreen
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import android.util.Log
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pawstogether.model.UserRating
import com.example.pawstogether.ui.calendar.CalendarScreen
import com.example.pawstogether.ui.theme.PawsTogetherTheme
import com.example.pawstogether.ui.theme.screens.*
import com.example.pawstogether.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setContent {
            PawsTogetherTheme {
                val navController = rememberNavController()
                AppNavigator(navController)
            }
        }
    }

    @Composable
    fun AppNavigator(navController: NavHostController) {
        // Arranca en login (cámbialo a "calendar" si quieres ver el calendario directo)
        NavHost(navController = navController, startDestination = "login") {

            composable("calendar") {
                CalendarScreen() //  ya no recibe onDayClick
            }

            composable("login") {
                LoginScreen(
                    onEmailLogin = { email, password ->
                        signInWithEmail(email, password, navController)
                    },
                    onNavigateToRegister = { navController.navigate("register") }
                )
            }

            composable("profile") {
                ProfileScreen(
                    onProfileUpdated = {
                        Toast.makeText(
                            this@MainActivity,
                            "Perfil actualizado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }

            composable("register") {
                RegisterScreen(
                    onRegister = { email, password, petExperience, interests, services ->
                        registerWithEmail(
                            email,
                            password,
                            petExperience,
                            interests,
                            services,
                            navController
                        )
                    }
                )
            }

            composable("home") {
                HomeScreen(navController = navController)
            }

            composable("reports") {
                ReportsScreen(
                    onNavigateBack = { navController.navigateUp() },
                    navigateToSearch = { navController.navigate("search") }
                )
            }

            composable("adoption") {
                AdoptionScreen(navController = navController)
            }

            composable("services") {
                ServicesScreen(navController)
            }

            composable("PetCare") {
                PetCareScreen(navController)
            }

            composable("chats_list") {
                val chatViewModel: ChatViewModel = viewModel()
                ChatsListScreen(
                    viewModel = chatViewModel,
                    onChatClick = { userId, userName ->
                        navController.navigate("chat/$userId/$userName")
                    }
                )
            }

            composable(
                "chat/{userId}/{userName}",
                arguments = listOf(
                    navArgument("userId") { type = NavType.StringType },
                    navArgument("userName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                val userName = backStackEntry.arguments?.getString("userName") ?: ""
                val chatViewModel: ChatViewModel = viewModel()
                ChatScreen(
                    viewModel = chatViewModel,
                    otherUserId = userId,
                    otherUserName = userName,
                    navigateUp = { navController.navigateUp() }
                )
            }

            composable("veterinary_list") {
                VeterinaryListScreen(navController)
            }

            composable(
                route = "rating/{userId}/{serviceType}",
                arguments = listOf(
                    navArgument("userId") { type = NavType.StringType },
                    navArgument("serviceType") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                val serviceType = backStackEntry.arguments?.getString("serviceType") ?: ""
                val userName = "NombreUsuario"

                RatingScreen(
                    toUserId = userId,
                    serviceType = serviceType,
                    userName = userName,
                    onRatingSubmit = { rating ->
                        saveRatingToFirebase(rating) {
                            navController.popBackStack()
                        }
                    },
                    onClose = {
                        navController.popBackStack()
                    }
                )
            }
            composable("chatbot") { ChatBotScreen(navController) }

        }
    }

    private fun saveRatingToFirebase(rating: UserRating, onComplete: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("ratings")
            .add(rating)
            .addOnSuccessListener {
                updateUserRating(rating.toUserId)
                onComplete()
            }
            .addOnFailureListener { e ->
                Log.e("MainActivity", "Error al guardar la reseña", e)
                onComplete()
            }
    }

    private fun updateUserRating(userId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("ratings")
            .whereEqualTo("toUserId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val totalStars = documents.sumOf { it.getLong("stars")?.toInt() ?: 0 }
                val averageRating =
                    if (documents.size() > 0) totalStars.toFloat() / documents.size() else 0f

                db.collection("users").document(userId)
                    .update("averageRating", averageRating)
            }
    }

    private fun signInWithEmail(email: String, password: String, navController: NavHostController) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    val errorMessage = task.exception?.message ?: "Error desconocido"
                    Toast.makeText(
                        baseContext,
                        "Inicio de sesión fallido: $errorMessage",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun registerWithEmail(
        email: String,
        password: String,
        petExperience: String,
        interests: String,
        services: String,
        navController: NavHostController
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val userName = email.substringBefore("@")
                        val userInfo = hashMapOf(
                            "email" to email,
                            "userName" to userName,
                            "petExperience" to petExperience,
                            "interests" to interests,
                            "services" to services,
                            "averageRating" to 0f
                        )

                        db.collection("users").document(user.uid).set(userInfo)
                            .addOnSuccessListener {
                                navController.navigate("home") {
                                    popUpTo("register") { inclusive = true }
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    baseContext,
                                    "Error al guardar la información: ${e.message}}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                } else {
                    val errorMessage = task.exception?.message ?: "Error desconocido"
                    Toast.makeText(
                        baseContext,
                        "Registro fallido: $errorMessage",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
