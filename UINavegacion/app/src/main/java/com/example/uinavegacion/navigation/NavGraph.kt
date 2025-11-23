package com.example.uinavegacion.navigation
import androidx.compose.foundation.layout.padding // Para aplicar innerPadding
import androidx.compose.material3.Scaffold // Estructura base con slots
import androidx.compose.runtime.Composable // Marcador composable
import androidx.compose.ui.Modifier // Modificador
import androidx.navigation.NavHostController // Controlador de navegación
import androidx.navigation.compose.NavHost // Contenedor de destinos
import androidx.navigation.compose.composable // Declarar cada destino
import kotlinx.coroutines.launch // Para abrir/cerrar drawer con corrutinas

import androidx.compose.material3.ModalNavigationDrawer // Drawer lateral modal
import androidx.compose.material3.rememberDrawerState // Estado del drawer
import androidx.compose.material3.DrawerValue // Valores (Opened/Closed)
import androidx.compose.runtime.rememberCoroutineScope // Alcance de corrutina
import androidx.navigation.NavType
import androidx.navigation.navArgument


import com.example.uinavegacion.ui.components.AppTopBar // Barra superior
import com.example.uinavegacion.ui.components.AppDrawer // Drawer composable
import com.example.uinavegacion.ui.components.defaultDrawerItems // Ítems por defecto
import com.example.uinavegacion.ui.screen.HomeScreen // Pantalla Home
import com.example.uinavegacion.ui.screen.LoginScreenVm // Pantalla Login
import com.example.uinavegacion.ui.screen.PostAddEditContent
import com.example.uinavegacion.ui.screen.PostSearchContent
import com.example.uinavegacion.ui.screen.PostsListContent
import com.example.uinavegacion.ui.screen.RegisterScreenVm // Pantalla Registro
import com.example.uinavegacion.ui.viewmodel.AuthViewModel


import com.example.uinavegacion.ui.screen.PostsScreen // <-- primero. 1. NUEVO
import com.example.uinavegacion.ui.viewmodel.PostViewModel

@Composable // Gráfico de navegación + Drawer + Scaffold
fun AppNavGraph(navController: NavHostController,
                authViewModel: AuthViewModel,        // <--  recibimos el VM inyectado desde MainActivity
                postViewModel: PostViewModel = androidx.lifecycle.viewmodel.compose.viewModel() // VM de Posts reutilizable
     ) { // Recibe el controlador

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed) // Estado del drawer
    val scope = rememberCoroutineScope() // Necesario para abrir/cerrar drawer

    // Helpers de navegación (reutilizamos en topbar/drawer/botones)
    val goHome: () -> Unit    = { navController.navigate(Route.Home.path) }    // Ir a Home
    val goLogin: () -> Unit   = { navController.navigate(Route.Login.path) }   // Ir a Login
    val goRegister: () -> Unit = { navController.navigate(Route.Register.path) } // Ir a Registro
    val goPosts: () -> Unit = { navController.navigate(Route.PostsList.path) }
    val goSearchPost: () -> Unit = { navController.navigate(Route.PostSearch.path) }
    val goAddPost: () -> Unit = { navController.navigate("posts/addedit") }
    val goBack: () -> Unit = { navController.popBackStack() }

    ModalNavigationDrawer( // Capa superior con drawer lateral
        drawerState = drawerState, // Estado del drawer
        drawerContent = { // Contenido del drawer (menú)
            AppDrawer( // Nuestro componente Drawer
                currentRoute = null, // Puedes pasar navController.currentBackStackEntry?.destination?.route
                items = defaultDrawerItems( // Lista estándar
                    onHome = {
                        scope.launch { drawerState.close() } // Cierra drawer
                        goHome() // Navega a Home
                    },
                    onLogin = {
                        scope.launch { drawerState.close() } // Cierra drawer
                        goLogin() // Navega a Login
                    },
                    onRegister = {
                        scope.launch { drawerState.close() } // Cierra drawer
                        goRegister() // Navega a Registro
                    },
                    onPosts = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Route.PostsList.path)
                    },
                    onPostSearch = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Route.PostSearch.path)
                    }

                )
            )
        }
    ) {
        Scaffold( // Estructura base de pantalla
            topBar = { // Barra superior con íconos/menú
                AppTopBar(
                    onOpenDrawer = { scope.launch { drawerState.open() } }, // Abre drawer
                    onHome = goHome,     // Botón Home
                    onLogin = goLogin,   // Botón Login
                    onRegister = goRegister, // Botón Registro
                    onPosts = { navController.navigate(Route.PostsList.path) },
                    onSearchPost = { navController.navigate(Route.PostSearch.path) },
                    onAddPost = { navController.navigate("posts/addedit") }

                )
            }
        ) { innerPadding -> // Padding que evita solapar contenido
            NavHost( // Contenedor de destinos navegables
                navController = navController, // Controlador
                startDestination = Route.Home.path, // Inicio: Home
                modifier = Modifier.padding(innerPadding) // Respeta topBar
            ) {
                composable(Route.Home.path) { // Destino Home
                    HomeScreen(
                        onGoLogin = goLogin,     // Botón para ir a Login
                        onGoRegister = goRegister // Botón para ir a Registro
                    )
                }
                composable(Route.Login.path) { // Destino Login
                    //1 modificamos el acceso a la pagina
                    // Usamos la versión con ViewModel (LoginScreenVm) para formularios/validación en tiempo real
                    LoginScreenVm(
                        vm = authViewModel,            // <-- NUEVO: pasamos VM inyectado
                        onLoginOkNavigateHome = goHome,            // Si el VM marca success=true, navegamos a Home
                        onGoRegister = goRegister                  // Enlace para ir a la pantalla de Registro
                    )
                }
                composable(Route.Register.path) { // Destino Registro
                    //2 modificamos el acceso a la pagina
                    // Usamos la versión con ViewModel (RegisterScreenVm) para formularios/validación en tiempo real
                    RegisterScreenVm(
                        vm = authViewModel,            // <-- NUEVO: pasamos VM inyectado
                        onRegisteredNavigateLogin = goLogin,       // Si el VM marca success=true, volvemos a Login
                        onGoLogin = goLogin                        // Botón alternativo para ir a Login
                    )
                }

                // NUEVO: Listado de Posts con acciones Editar/Eliminar y navegación a Agregar.
                composable(Route.PostsList.path) {
                    PostsListContent(
                        vm = postViewModel,
                        onAdd = { navController.navigate("posts/addedit") },
                        onEdit = { id -> navController.navigate("posts/addedit?postId=$id") }
                    )
                }

                // NUEVO: Búsqueda por ID, resultado siempre visible y con scroll.
                composable(Route.PostSearch.path) {
                    PostSearchContent(vm = postViewModel)
                }

                // NUEVO: Agregar/Editar con argumento opcional postId (query).
                composable(
                    route = "posts/addedit?postId={postId}",
                    arguments = listOf(
                        navArgument("postId") {
                            type = NavType.IntType
                            defaultValue = -1
                            nullable = false
                        }
                    )
                ) { entry ->
                    // Leemos el argumento: -1 significa que venimos a crear.
                    val id = entry.arguments?.getInt("postId") ?: -1
                    PostAddEditContent(
                        vm = postViewModel,
                        postId = if (id <= 0) null else id,
                        onSaved = {
                            // Al guardar, recargamos la lista y volvemos a la pantalla anterior.
                            postViewModel.loadPosts()
                            navController.popBackStack()
                        },
                        onCancel = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}