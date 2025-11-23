package com.example.amilimetros.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.amilimetros.ui.viewmodel.CartViewModel
import com.example.amilimetros.data.local.storage.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
    userPreferences: UserPreferences
) {
    val scope = rememberCoroutineScope()
    val items by cartViewModel.items.collectAsState()
    val total by cartViewModel.total.collectAsState()
    val isLoading by cartViewModel.isLoading.collectAsState()
    val error by cartViewModel.error.collectAsState()
    val successMessage by cartViewModel.successMessage.collectAsState()

    var userId by remember { mutableStateOf<Long?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userId = userPreferences.getUserId()
        userId?.let { cartViewModel.cargarCarrito(it) }
    }

    // Mostrar mensajes
    LaunchedEffect(error) {
        error?.let {
            // Mostrar snackbar con error
            cartViewModel.clearMessages()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            // Mostrar snackbar con éxito
            cartViewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Carrito") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    if (items.isNotEmpty()) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, "Vaciar carrito")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                items.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(120.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Tu carrito está vacío",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Agrega productos para comenzar",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = { navController.navigate("products") }) {
                            Text("Ver Productos")
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Lista de productos
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(items) { item ->
                                CartItemCard(
                                    item = item,
                                    onUpdateQuantity = { newQuantity ->
                                        userId?.let {
                                            cartViewModel.actualizarCantidad(item.id, newQuantity, it)
                                        }
                                    },
                                    onRemove = {
                                        userId?.let {
                                            cartViewModel.eliminarItem(item.id, it)
                                        }
                                    }
                                )
                            }
                        }

                        // Resumen y botón de compra
                        Surface(
                            shadowElevation = 8.dp,
                            tonalElevation = 2.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Total:",
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Text(
                                        text = "$${String.format("%,.0f", total)}",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        // TODO: Implementar proceso de compra
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.ShoppingCart, null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Proceder a Compra")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo para confirmar vaciar carrito
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Vaciar Carrito") },
            text = { Text("¿Estás seguro de que deseas vaciar todo el carrito?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        userId?.let { cartViewModel.vaciarCarrito(it) }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Sí, vaciar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun CartItemCard(
    item: com.example.amilimetros.data.remote.dto.CarritoItemDto,
    onUpdateQuantity: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen placeholder
            Surface(
                modifier = Modifier.size(80.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.productoNombre,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${String.format("%,.0f", item.productoPrecio)} c/u",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Controles de cantidad
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (item.cantidad > 1) {
                                onUpdateQuantity(item.cantidad - 1)
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Remove, "Disminuir")
                    }

                    Text(
                        text = "${item.cantidad}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    IconButton(
                        onClick = { onUpdateQuantity(item.cantidad + 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Add, "Aumentar")
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = onRemove) {
                        Icon(
                            Icons.Default.Delete,
                            "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}