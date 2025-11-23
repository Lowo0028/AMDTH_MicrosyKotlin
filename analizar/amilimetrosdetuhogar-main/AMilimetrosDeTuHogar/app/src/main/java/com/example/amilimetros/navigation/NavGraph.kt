package com.example.amilimetros.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.amilimetros.data.local.storage.UserPreferences
import com.example.amilimetros.data.repository.*
import com.example.amilimetros.ui.screen.*
import com.example.amilimetros.ui.viewmodel.*

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Route.Home.path
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }

    // Repositorios
    val authRepository = remember { AuthApiRepository() }
    val productoRepository = remember { ProductoApiRepository() }
    val carritoRepository = remember { CarritoApiRepository() }
    val animalRepository = remember { AnimalApiRepository() }
    val formularioRepository = remember { FormularioApiRepository() }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ========== HOME ==========
        composable(Route.Home.path) {
            HomeScreen(
                onNavigateToLogin = { navController.navigate(Route.Login.path) },
                onNavigateToProducts = { navController.navigate(Route.Products.path) },
                onNavigateToAnimals = { navController.navigate(Route.Animals.path) },
                onNavigateToCart = { navController.navigate(Route.Cart.path) },
                onNavigateToProfile = { navController.navigate(Route.Profile.path) },
                onNavigateToLocation = { navController.navigate(Route.Location.path) },
                onNavigateToAdmin = { navController.navigate(Route.Admin.path) }
            )
        }

        // ========== LOGIN ==========
        composable(Route.Login.path) {
            val authViewModel = remember { AuthViewModel(authRepository, userPrefs) }

            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Route.Register.path)
                },
                onNavigateToHome = {
                    navController.navigate(Route.Home.path) {
                        popUpTo(Route.Login.path) { inclusive = true }
                    }
                }
            )
        }

        // ========== REGISTRO ==========
        composable(Route.Register.path) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Route.Login.path) {
                        popUpTo(Route.Register.path) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ========== PRODUCTOS ==========
        composable(Route.Products.path) {
            ProductListScreen()
        }

        // ========== ANIMALES ==========
        composable(Route.Animals.path) {
            AnimalListScreen(
                onNavigateToAdoptionForm = { animalId ->
                    navController.navigate(Route.AdoptionForm.createRoute(animalId))
                }
            )
        }

        // ========== CARRITO ==========
        composable(Route.Cart.path) {
            val cartViewModel = remember { CartViewModel(carritoRepository) }

            CartScreen(
                navController = navController,
                cartViewModel = cartViewModel,
                userPreferences = userPrefs
            )
        }

        // ========== FORMULARIO DE ADOPCIÓN ==========
        composable(
            route = Route.AdoptionForm.path,
            arguments = listOf(navArgument("animalId") { type = NavType.LongType })
        ) { backStackEntry ->
            val animalId = backStackEntry.arguments?.getLong("animalId") ?: 0L
            val adoptionViewModel = remember { AdoptionFormViewModel(formularioRepository, userPrefs) }
            val animalViewModel = remember { AnimalViewModel(animalRepository) }

            // Cargar nombre del animal
            LaunchedEffect(animalId) {
                animalViewModel.loadAnimalById(animalId)
            }

            val animalName = animalViewModel.selectedAnimal.value?.nombre ?: "Animal"

            AdoptionFormScreen(
                animalId = animalId,
                animalName = animalName,
                viewModel = adoptionViewModel,
                onSubmitSuccess = {
                    navController.navigate(Route.Animals.path) {
                        popUpTo(Route.Animals.path) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ========== PERFIL ==========
        composable(Route.Profile.path) {
            val profileViewModel = remember { ProfileViewModel(authRepository, userPrefs) }

            ProfileScreen(
                navController = navController,
                userPreferences = userPrefs,
                onLogout = {
                    navController.navigate(Route.Home.path) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ========== HISTORIAL DE COMPRAS ==========
        composable(Route.OrderHistory.path) {
            val orderHistoryViewModel = remember { OrderHistoryViewModel(userPrefs) }

            OrderHistoryScreen(
                viewModel = orderHistoryViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ========== SOLICITUDES DE ADOPCIÓN ==========
        composable(Route.AdoptionRequests.path) {
            val adoptionRequestsViewModel = remember { AdoptionRequestsViewModel() }

            AdoptionRequestsScreen(
                viewModel = adoptionRequestsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ========== UBICACIÓN ==========
        composable(Route.Location.path) {
            LocationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ========== ADMIN ==========
        composable(Route.Admin.path) {
            val adminViewModel = remember { AdminViewModel(productoRepository, animalRepository) }

            AdminScreen(
                viewModel = adminViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}