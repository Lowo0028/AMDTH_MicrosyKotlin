package com.example.amilimetros.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.amilimetros.R
import com.example.amilimetros.data.local.storage.UserPreferences
import com.example.amilimetros.data.remote.dto.AnimalDto
import com.example.amilimetros.data.repository.AnimalApiRepository
import com.example.amilimetros.ui.viewmodel.AnimalViewModel
import kotlinx.coroutines.launch

@Composable
fun AnimalListScreen(onNavigateToAdoptionForm: (Long) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPrefs = remember { UserPreferences(context) }

    val animalRepository = remember { AnimalApiRepository() }
    val viewModel = remember { AnimalViewModel(animalRepository) }

    val animales by viewModel.animales.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var isLoggedIn by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoggedIn = userPrefs.getIsLoggedIn()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🐾 Animales en Adopción", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            IconButton(onClick = { viewModel.cargarAnimalesDisponibles() }) {
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
                    Button(onClick = { viewModel.cargarAnimalesDisponibles() }) { Text("Reintentar") }
                }
            }
            animales.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay animales disponibles")
            }
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(animales) { animal ->
                    AnimalCard(
                        animal = animal,
                        isLoggedIn = isLoggedIn,
                        onAdopt = {
                            if (isLoggedIn) onNavigateToAdoptionForm(animal.id)
                            else showInfoDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            confirmButton = { TextButton(onClick = { showInfoDialog = false }) { Text("Entendido") } },
            title = { Text("💡 Consejo") },
            text = { Text("Inicia sesión para adoptar.\n\nTus datos se rellenarán automáticamente 💚") },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun AnimalCard(animal: AnimalDto, isLoggedIn: Boolean, onAdopt: () -> Unit) {
    ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text(animal.nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("${animal.especie} • ${animal.raza}", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(4.dp))
                Text(animal.edad, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                Text(animal.descripcion, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(12.dp))
                Button(onClick = onAdopt, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.Favorite, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Solicitar Adopción")
                }
            }
            Spacer(Modifier.width(12.dp))
            val drawableRes = when (animal.especie.lowercase()) {
                "perro" -> R.drawable.perro_placeholder
                "gato" -> R.drawable.gato_placeholder
                "conejo" -> R.drawable.conejo_placeholder
                else -> R.drawable.animal_placeholder
            }
            Image(painter = painterResource(drawableRes), contentDescription = null, modifier = Modifier.size(100.dp))
        }
    }
}