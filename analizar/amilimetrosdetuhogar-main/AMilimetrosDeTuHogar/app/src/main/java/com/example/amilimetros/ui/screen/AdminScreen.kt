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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.amilimetros.data.remote.dto.ProductoDto
import com.example.amilimetros.data.remote.dto.AnimalDto
import com.example.amilimetros.ui.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: AdminViewModel,
    onNavigateBack: () -> Unit
) {
    val productos by viewModel.productos.collectAsState()
    val animales by viewModel.animales.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Productos", "Animales", "Formularios")

    LaunchedEffect(Unit) {
        viewModel.cargarProductos()
        viewModel.cargarAnimales()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administrador") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // TABS
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            // CONTENIDO
            when (selectedTab) {
                0 -> AdminProductsTab(productos, isLoading, error, viewModel)
                1 -> AdminAnimalsTab(animales, isLoading, error, viewModel)
                2 -> AdminFormsTab()
            }

            // MENSAJES
            successMessage?.let {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearMessages() }) {
                            Text("OK")
                        }
                    }
                ) {
                    Text(it)
                }
            }
        }
    }
}

@Composable
fun AdminProductsTab(
    productos: List<ProductoDto>,
    isLoading: Boolean,
    error: String?,
    viewModel: AdminViewModel
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Gestión de Productos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            IconButton(onClick = { viewModel.cargarProductos() }) {
                Icon(Icons.Default.Refresh, "Recargar")
            }
        }

        Spacer(Modifier.height(16.dp))

        when {
            isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            error != null -> Text(error, color = MaterialTheme.colorScheme.error)
            productos.isEmpty() -> Text("No hay productos")
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(productos) { producto ->
                    AdminProductCard(producto, onDelete = {
                        // Implementar eliminación
                    })
                }
            }
        }
    }
}

@Composable
fun AdminProductCard(producto: ProductoDto, onDelete: () -> Unit) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, fontWeight = FontWeight.Bold)
                Text("$${producto.precio}", color = MaterialTheme.colorScheme.primary)
                Text(producto.categoria, style = MaterialTheme.typography.bodySmall)
            }
            Row {
                IconButton(onClick = { /* Editar */ }) {
                    Icon(Icons.Default.Edit, "Editar", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun AdminAnimalsTab(
    animales: List<AnimalDto>,
    isLoading: Boolean,
    error: String?,
    viewModel: AdminViewModel
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Gestión de Animales", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            IconButton(onClick = { viewModel.cargarAnimales() }) {
                Icon(Icons.Default.Refresh, "Recargar")
            }
        }

        Spacer(Modifier.height(16.dp))

        when {
            isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            error != null -> Text(error, color = MaterialTheme.colorScheme.error)
            animales.isEmpty() -> Text("No hay animales")
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(animales) { animal ->
                    AdminAnimalCard(animal, onDelete = {
                        // Implementar eliminación
                    })
                }
            }
        }
    }
}

@Composable
fun AdminAnimalCard(animal: AnimalDto, onDelete: () -> Unit) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(animal.nombre, fontWeight = FontWeight.Bold)
                Text("${animal.especie} - ${animal.raza}", style = MaterialTheme.typography.bodyMedium)
                Text(animal.edad, style = MaterialTheme.typography.bodySmall)
                if (animal.isAdoptado) {
                    AssistChip(onClick = {}, label = { Text("Adoptado") })
                }
            }
            Row {
                IconButton(onClick = { /* Editar */ }) {
                    Icon(Icons.Default.Edit, "Editar", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun AdminFormsTab() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Gestión de Formularios (Próximamente)")
    }
}