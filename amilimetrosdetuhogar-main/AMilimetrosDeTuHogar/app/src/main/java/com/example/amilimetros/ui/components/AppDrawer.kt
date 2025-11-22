package com.example.amilimetros.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class DrawerItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun AppDrawer(
    currentRoute: String?,
    items: List<DrawerItem>,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(modifier = modifier) {
        Spacer(Modifier.height(16.dp))

        Text(
            text = "Menú de Navegación",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        items.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.label) },
                selected = false,
                onClick = item.onClick,
                icon = { Icon(item.icon, contentDescription = item.label) },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun defaultDrawerItems(
    onHome: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onProducts: () -> Unit,
    onAnimals: () -> Unit,
    onCart: () -> Unit,
    onAdmin: (() -> Unit)? = null,
    onProfile: (() -> Unit)? = null,          // ✅ NUEVO
    onOrderHistory: (() -> Unit)? = null,     // ✅ NUEVO
    isLoggedIn: Boolean,
    isAdmin: Boolean
): List<DrawerItem> {
    val items = mutableListOf<DrawerItem>()

    // Siempre visible
    items.add(DrawerItem("Inicio", Icons.Filled.Home, onHome))

    if (isLoggedIn) {
        // Usuario logueado
        items.add(DrawerItem("Productos", Icons.Filled.Store, onProducts))      // ✅ CORREGIDO
        items.add(DrawerItem("Animales", Icons.Filled.Pets, onAnimals))
        items.add(DrawerItem("Carrito", Icons.Filled.ShoppingCart, onCart))     // ✅ CORREGIDO

        // ✅ NUEVO: Perfil e Historial
        if (onProfile != null) {
            items.add(DrawerItem("Mi Perfil", Icons.Filled.Person, onProfile))
        }
        if (onOrderHistory != null) {
            items.add(DrawerItem("Mis Compras", Icons.Filled.Receipt, onOrderHistory))
        }

        if (isAdmin && onAdmin != null) {
            items.add(DrawerItem("Panel Admin", Icons.Filled.AdminPanelSettings, onAdmin))
        }
    } else {
        // Usuario NO logueado
        items.add(DrawerItem("Iniciar Sesión", Icons.Filled.Login, onLogin))
        items.add(DrawerItem("Registrarse", Icons.Filled.PersonAdd, onRegister))
    }

    return items
}