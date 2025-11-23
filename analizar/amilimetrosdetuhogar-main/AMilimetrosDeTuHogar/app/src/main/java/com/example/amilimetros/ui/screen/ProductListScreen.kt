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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.amilimetros.data.local.storage.UserPreferences
import com.example.amilimetros.data.remote.dto.ProductoDto
import com.example.amilimetros.data.repository.ProductoApiRepository
import com.example.amilimetros.data.repository.CarritoApiRepository
import com.example.amilimetros.ui.viewmodel.ProductViewModel
import com.example.amilimetros.ui.viewmodel.CartViewModel
import kotlinx.coroutines.launch

@Composable
fun ProductListScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPrefs = remember { UserPreferences(context) }

    val productoRepository = remember { ProductoApiRepository() }
    val carritoRepository = remember { CarritoApiRepository() }

    val productViewModel = remember { ProductViewModel(productoRepository) }
    val cartViewModel = remember { CartViewModel(carritoRepository) }

    val productos by productViewModel.productos.collectAsState()
    val isLoading by productViewModel.isLoading.collectAsState()
    val error by productViewModel.error.collectAsState()

    var userId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        userId = userPrefs.getUserId()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🛒 Productos", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            IconButton(onClick = { productViewModel.cargarProductos() }) {
                Icon(Icons.Filled.Refresh, "Recargar")
            }
        }

        Spacer(Modifier.height(16.dp))

        when {
            isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { productViewModel.cargarProductos() }) { Text("Reintentar") }
                }
            }
            productos.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay productos disponibles")
            }
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(productos) { product ->
                    ProductCard(
                        product = product,
                        onAddToCart = {
                            userId?.let {
                                cartViewModel.agregarAlCarrito(it, product.id)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductCard(product: ProductoDto, onAddToCart: () -> Unit) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(product.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(product.descripcion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("$${"%.0f".format(product.precio)}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    AssistChip(onClick = { }, label = { Text(product.categoria) })
                }
            }
            Spacer(Modifier.width(12.dp))
            FilledTonalButton(onClick = onAddToCart) {
                Icon(Icons.Filled.AddShoppingCart, null, modifier = Modifier.size(20.dp))
            }
        }
    }
}