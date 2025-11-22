package com.example.amilimetros.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.amilimetros.data.local.database.AppDatabase
import com.example.amilimetros.data.local.storage.UserPreferences
import com.example.amilimetros.data.repository.*
import com.example.amilimetros.ui.screen.*
import com.example.amilimetros.ui.viewmodel.*
import kotlinx.coroutines.launch

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Route.Home.path
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPrefs = remember { UserPreferences(context) }

    // ✅ Lazy initialization
    val db = remember { AppDatabase.getInstance(context) }
    val userRepo = remember { UserRepository(db.userDao()) }
    val productRepo = remember { ProductRepository(db.productDao()) }
    val animalRepo = remember { AnimalRepository(db.animalDao()) }
    val cartRepo = remember { CartRepository(db.cartDao()) }

    // ViewModels
    val authVm: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(userRepo)
    )
    val productVm: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(productRepo, cartRepo)
    )
    val animalVm: AnimalViewModel = viewModel(
        factory = AnimalViewModelFactory(animalRepo)
    )

    // Estados de sesión
    val userId by userPrefs.userId.collectAsState(initial = 0L)
    val userName by userPrefs.userName.collectAsState(initial = "")
    val userEmail by userPrefs.userEmail.collectAsState(initial = "")
    val userPhone by userPrefs.userPhone.collectAsState(initial = "")

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ========== HOME ==========
        composable(Route.Home.path) {
            HomeScreen(
                onGoLogin = { navController.navigate(Route.Login.path) },
                onGoRegister = { navController.navigate(Route.Register.path) },
                onGoProducts = { navController.navigate(Route.Products.path) },
                onGoAnimals = { navController.navigate(Route.Animals.path) },
                onGoCart = { navController.navigate(Route.Cart.path) },
                onGoAdmin = { navController.navigate(Route.Admin.path) },
                onLogout = {
                    scope.launch {
                        userPrefs.logout()
                        navController.navigate(Route.Home.path) {
                            popUpTo(Route.Home.path) { inclusive = true }
                        }
                    }
                }
            )
        }

        // ========== LOGIN ==========
        composable(Route.Login.path) {
            LoginScreenVm(
                vm = authVm,
                onLoginOkNavigateHome = {
                    navController.navigate(Route.Home.path) {
                        popUpTo(Route.Login.path) { inclusive = true }
                    }
                },
                onGoRegister = {
                    navController.navigate(Route.Register.path) {
                        popUpTo(Route.Login.path) { inclusive = true }
                    }
                }
            )
        }

        // ========== REGISTRO ==========
        composable(Route.Register.path) {
            RegisterScreenVm(
                vm = authVm,
                onRegisteredNavigateLogin = {
                    navController.navigate(Route.Login.path) {
                        popUpTo(Route.Register.path) { inclusive = true }
                    }
                },
                onGoLogin = {
                    navController.navigate(Route.Login.path) {
                        popUpTo(Route.Register.path) { inclusive = true }
                    }
                }
            )
        }

        // ========== PRODUCTOS ==========
        composable(Route.Products.path) {
            ProductListScreen(vm = productVm)
        }

        // ========== CARRITO ==========
        composable(Route.Cart.path) {
            CartScreen()
        }

        // ========== ANIMALES ==========
        composable(Route.Animals.path) {
            AnimalListScreen(
                vm = animalVm,
                authVm = authVm,
                onNavigateToAdoptionForm = { animalId ->
                    navController.navigate(Route.AdoptionForm.createRoute(animalId))
                }
            )
        }

        // ========== ADMIN ==========
        composable(Route.Admin.path) {
            AdminScreen(
                productVm = productVm,
                animalVm = animalVm
            )
        }

        // ========== PERFIL ✅ NUEVO ==========
        composable(Route.Profile.path) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    scope.launch {
                        userPrefs.logout()
                        navController.navigate(Route.Home.path) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }

        // ========== HISTORIAL DE COMPRAS ✅ NUEVO ==========
        composable(Route.OrderHistory.path) {
            OrderHistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ========== FORMULARIO DE ADOPCIÓN ==========
        composable(
            route = Route.AdoptionForm.path,
            arguments = listOf(navArgument("animalId") { type = NavType.LongType })
        ) { backStackEntry ->
            val animalId = backStackEntry.arguments?.getLong("animalId") ?: 0L

            // Buscar el nombre del animal
            val animals by animalVm.allAnimals.collectAsState()
            val animal = animals.find { it.id == animalId }

            AdoptionFormScreen(
                animalId = animalId,
                animalName = animal?.name ?: "Animal",
                userId = userId,
                userName = userName,
                userEmail = userEmail,
                userPhone = userPhone,
                onSubmitSuccess = {
                    navController.navigate(Route.Animals.path) {
                        popUpTo(Route.Animals.path) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ========== UBICACIÓN (MAPA) ==========
        composable(Route.Location.path) {
            LocationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}