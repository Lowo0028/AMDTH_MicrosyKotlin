package com.example.amilimetros.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    onOpenDrawer: () -> Unit,
    onHome: () -> Unit,
    onProducts: () -> Unit,
    onAnimals: () -> Unit,
    onCart: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = {
            Text(
                text = "🐾",
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menú")
            }
        },
        actions = {
            IconButton(onClick = onHome) {
                Icon(Icons.Filled.Home, contentDescription = "Inicio")
            }
            // 🛍️ TIENDA (antes era ShoppingCart - CORREGIDO)
            IconButton(onClick = onProducts) {
                Icon(Icons.Filled.Store, contentDescription = "Tienda")
            }
            IconButton(onClick = onAnimals) {
                Icon(Icons.Filled.Pets, contentDescription = "Animales")
            }
            // 🛒 CARRITO (antes era ShoppingBag - CORREGIDO)
            IconButton(onClick = onCart) {
                Icon(Icons.Filled.ShoppingCart, contentDescription = "Carrito")
            }
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "Más")
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Inicio") },
                    onClick = { showMenu = false; onHome() },
                    leadingIcon = { Icon(Icons.Filled.Home, null) }
                )
                DropdownMenuItem(
                    text = { Text("Tienda") },
                    onClick = { showMenu = false; onProducts() },
                    leadingIcon = { Icon(Icons.Filled.Store, null) }
                )
                DropdownMenuItem(
                    text = { Text("Animales") },
                    onClick = { showMenu = false; onAnimals() },
                    leadingIcon = { Icon(Icons.Filled.Pets, null) }
                )
                DropdownMenuItem(
                    text = { Text("Carrito") },
                    onClick = { showMenu = false; onCart() },
                    leadingIcon = { Icon(Icons.Filled.ShoppingCart, null) }
                )
            }
        }
    )
}

@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var imageBitmap by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }

    LaunchedEffect(Unit) {
        val db = com.example.amilimetros.data.local.database.AppDatabase.getInstance(context)
        val logoDao = db.logoDao()

        val logoEntity = logoDao.getLogo()
        logoEntity?.let {
            val bitmap = BitmapFactory.decodeByteArray(it.image, 0, it.image.size)
            imageBitmap = bitmap.asImageBitmap()
        }
    }

    imageBitmap?.let {
        Image(
            bitmap = it,
            contentDescription = "Logo AMilímetros",
            modifier = modifier.size(120.dp)
        )
    } ?: run {
        Text("Logo no disponible", style = MaterialTheme.typography.bodyMedium)
    }
}