package com.example.amilimetros.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.amilimetros.data.local.cart.CartItemEntity
import com.example.amilimetros.data.local.storage.UserPreferences
import com.example.amilimetros.ui.viewmodel.CartViewModel
import com.example.amilimetros.ui.viewmodel.CartViewModelFactory
import com.example.amilimetros.data.repository.CartRepository
import com.example.amilimetros.data.repository.OrderRepository  // ✅ NUEVO
import com.example.amilimetros.data.repository.ProductRepository // ✅ NUEVO
import com.example.amilimetros.data.local.database.AppDatabase

@Composable
fun CartScreen() {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val userId by userPrefs.userId.collectAsStateWithLifecycle(0L)

    // Log para debug
    LaunchedEffect(userId) {
        Log.d("CartScreen", "UserId: $userId")
    }

    // ✅ ACTUALIZADO: Crear repositorios necesarios
    val db = remember { AppDatabase.getInstance(context) }
    val cartRepo = remember { CartRepository(db.cartDao()) }
    val orderRepo = remember { OrderRepository(db.orderDao(), db.orderItemDao()) }    // ✅ NUEVO
    val productRepo = remember { ProductRepository(db.productDao()) }                  // ✅ NUEVO

    // IMPORTANTE: Solo crear ViewModel si userId > 0
    if (userId == 0L) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    Icons.Filled.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Debes iniciar sesión para ver tu carrito",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    // ✅ ACTUALIZADO: Pasar los 4 parámetros al Factory
    val vm: CartViewModel = viewModel(
        factory = CartViewModelFactory(
            cartRepository = cartRepo,
            orderRepository = orderRepo,      // ✅ NUEVO
            productRepository = productRepo,  // ✅ NUEVO
            userId = userId
        )
    )

    val cartItems by vm.cartItems.collectAsStateWithLifecycle()
    val total by vm.total.collectAsStateWithLifecycle()

    // Log para debug
    LaunchedEffect(cartItems) {
        Log.d("CartScreen", "Cart items count: ${cartItems.size}")
        cartItems.forEach { item ->
            Log.d("CartScreen", "Item: ${item.productName}, qty: ${item.quantity}, price: ${item.productPrice}")
        }
    }

    var showClearDialog by remember { mutableStateOf(false) }
    var showCheckoutDialog by remember { mutableStateOf(false) }  // ✅ NUEVO: Confirmación de compra

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // ✅ ACTUALIZADO: Icono corregido
        Text(
            text = "🛒 Mi Carrito",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Filled.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Tu carrito está vacío",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartItems, key = { it.id }) { item ->
                    CartItemCard(
                        item = item,
                        onIncrement = { vm.incrementQuantity(item) },
                        onDecrement = { vm.decrementQuantity(item) },
                        onRemove = { vm.removeItem(item) }
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // ========== TOTAL ==========
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total:",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$${"%.2f".format(total ?: 0.0)}",  // ✅ MEJORADO: Formato de precio
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(16.dp))

            // ========== BOTONES ==========
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { showClearDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.DeleteSweep, contentDescription = null)  // ✅ MEJORADO: Icono más claro
                    Spacer(Modifier.width(4.dp))
                    Text("Vaciar")
                }

                Button(
                    onClick = { showCheckoutDialog = true },  // ✅ CAMBIADO: Primero confirmar
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.ShoppingCartCheckout, contentDescription = null)  // ✅ NUEVO: Icono de checkout
                    Spacer(Modifier.width(4.dp))
                    Text("Comprar")
                }
            }
        }
    }

    // ========== DIÁLOGO CONFIRMAR VACIAR ==========
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            icon = { Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },  // ✅ NUEVO
            title = { Text("Vaciar Carrito") },
            text = { Text("¿Deseas eliminar todos los productos del carrito?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.clearCart()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // ✅ NUEVO: DIÁLOGO CONFIRMAR COMPRA
    if (showCheckoutDialog) {
        AlertDialog(
            onDismissRequest = { showCheckoutDialog = false },
            icon = {
                Icon(
                    Icons.Filled.ShoppingCart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("Confirmar Compra") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("¿Deseas finalizar la compra?")
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Total a pagar: $${"%.2f".format(total ?: 0.0)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "• Se creará un registro de tu compra\n• Se actualizará el stock de productos\n• Tu carrito se vaciará",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.checkout()
                        showCheckoutDialog = false
                    }
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Comprar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCheckoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun CartItemCard(
    item: CartItemEntity,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.productName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "$${"%.2f".format(item.productPrice)} c/u",  // ✅ MEJORADO: Formato
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Subtotal: $${"%.2f".format(item.productPrice * item.quantity)}",  // ✅ MEJORADO: Formato
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Control de cantidad
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = onDecrement) {
                        Icon(Icons.Filled.Remove, contentDescription = "Disminuir")
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "${item.quantity}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                                .widthIn(min = 32.dp)
                        )
                    }

                    IconButton(onClick = onIncrement) {
                        Icon(Icons.Filled.Add, contentDescription = "Aumentar")
                    }
                }

                // Botón eliminar
                FilledTonalButton(
                    onClick = onRemove,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Eliminar",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Quitar", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}