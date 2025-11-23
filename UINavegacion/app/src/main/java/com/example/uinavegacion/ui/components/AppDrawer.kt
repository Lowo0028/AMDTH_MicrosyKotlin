package com.example.uinavegacion.ui.components

import androidx.compose.material.icons.Icons // Íconos Material
import androidx.compose.material.icons.filled.Home // Ícono Home
import androidx.compose.material.icons.filled.AccountCircle // Ícono Login
import androidx.compose.material.icons.filled.Person // Ícono Registro
import androidx.compose.material3.Icon // Ícono en ítem del drawer
import androidx.compose.material3.NavigationDrawerItem // Ítem seleccionable
import androidx.compose.material3.NavigationDrawerItemDefaults // Defaults de estilo
import androidx.compose.material3.Text // Texto
import androidx.compose.material3.ModalDrawerSheet // Contenedor de contenido del drawer
import androidx.compose.runtime.Composable // Marcador composable
import androidx.compose.ui.Modifier // Modificador
import androidx.compose.ui.graphics.vector.ImageVector // Tipo de ícono
import androidx.compose.material.icons.filled.ListAlt // <-- NUEVO: icono para Posts
import androidx.compose.material.icons.filled.Search

// Pequeña data class para representar cada opción del drawer
data class DrawerItem( // Estructura de un ítem de menú lateral
    val label: String, // Texto a mostrar
    val icon: ImageVector, // Ícono del ítem
    val onClick: () -> Unit // Acción al hacer click
)

@Composable // Componente Drawer para usar en ModalNavigationDrawer
fun AppDrawer(
    currentRoute: String?, // Ruta actual (para marcar seleccionado si quieres)
    items: List<DrawerItem>, // Lista de ítems a mostrar
    modifier: Modifier = Modifier // Modificador opcional
) {
    ModalDrawerSheet( // Hoja que contiene el contenido del drawer
        modifier = modifier // Modificador encadenable
    ) {
        // Recorremos las opciones y pintamos ítems
        items.forEach { item -> // Por cada ítem
            NavigationDrawerItem( // Ítem con estados Material
                label = { Text(item.label) }, // Texto visible
                selected = false, // Puedes usar currentRoute == ... si quieres marcar
                onClick = item.onClick, // Acción al pulsar
                icon = { Icon(item.icon, contentDescription = item.label) }, // Ícono
                modifier = Modifier, // Sin mods extra
                colors = NavigationDrawerItemDefaults.colors() // Estilo por defecto
            )
        }
    }
}

// Helper para construir la lista estándar de ítems del drawer
@Composable
fun defaultDrawerItems(
    onHome: () -> Unit,   // Acción Home
    onLogin: () -> Unit,  // Acción Login
    onRegister: () -> Unit, // Acción Registro
    onPosts: () -> Unit, // <-- primero . 1. NUEVO
    onPostSearch: () -> Unit = {}     // nuevo con default
): List<DrawerItem> = listOf(
    DrawerItem("Home", Icons.Filled.Home, onHome),          // Ítem Home
    DrawerItem("Login", Icons.Filled.AccountCircle, onLogin),       // Ítem Login
    DrawerItem("Registro", Icons.Filled.Person, onRegister), // Ítem Registro
    DrawerItem("Posts", Icons.Filled.ListAlt, onPosts), // <-- segundo . 2 . NUEVO
    DrawerItem("Search", Icons.Filled.Search, onPostSearch) // <-- segundo . 2 . NUEVO
)