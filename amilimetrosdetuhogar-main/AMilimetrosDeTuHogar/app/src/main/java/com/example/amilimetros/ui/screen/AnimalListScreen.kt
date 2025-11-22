package com.example.amilimetros.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.amilimetros.R
import com.example.amilimetros.data.local.animal.AnimalEntity
import com.example.amilimetros.ui.viewmodel.AnimalViewModel
import com.example.amilimetros.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun AnimalListScreen(
    vm: AnimalViewModel,
    authVm: AuthViewModel,
    onNavigateToAdoptionForm: (Long) -> Unit,
    isUserLoggedIn: Boolean = false // ✅ nuevo parámetro (por ahora simulado)
) {
    val animals by vm.availableAnimals.collectAsStateWithLifecycle()
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // ✅ Solo mostrar el mensaje si NO ha iniciado sesión
    var showInfoDialog by remember { mutableStateOf(!isUserLoggedIn) }

    // Mensajes del ViewModel
    LaunchedEffect(uiState.successMsg) {
        uiState.successMsg?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearMessages()
        }
    }
    LaunchedEffect(uiState.errorMsg) {
        uiState.errorMsg?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "🐾 Animales en Adopción",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(16.dp))

                if (animals.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay animales disponibles para adopción")
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(animals) { animal ->
                            AnimatedAnimalCard(
                                animal = animal,
                                onAdopt = { onNavigateToAdoptionForm(animal.id) }
                            )
                        }
                    }
                }
            }

            // ✅ Mostrar el mensaje solo si no ha iniciado sesión
            if (showInfoDialog) {
                AlertDialog(
                    onDismissRequest = { showInfoDialog = false },
                    confirmButton = {
                        TextButton(onClick = { showInfoDialog = false }) {
                            Text("Entendido")
                        }
                    },
                    title = { Text("💡 Consejo") },
                    text = {
                        Text(
                            "Inicia sesión para comprar productos o adoptar.\n\n" +
                                    "Tus datos se rellenarán automáticamente al iniciar sesión 💚"
                        )
                    },
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }
}

@Composable
fun AnimatedAnimalCard(
    animal: AnimalEntity,
    onAdopt: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(500)) + scaleIn(tween(500))
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = animal.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "${animal.species} • ${animal.breed}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "${animal.age} año${if (animal.age != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = animal.description,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = onAdopt,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Favorite, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Solicitar Adopción de ${animal.name}")
                    }
                }

                Spacer(Modifier.width(12.dp))

                // Imagen local animada con fadeIn
                var imageVisible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay(200)
                    imageVisible = true
                }

                AnimatedVisibility(
                    visible = imageVisible,
                    enter = fadeIn(tween(600)) + scaleIn(tween(600))
                ) {
                    val drawableRes = when (animal.species.lowercase()) {
                        "perro" -> R.drawable.perro_placeholder
                        "gato" -> R.drawable.gato_placeholder
                        "conejo" -> R.drawable.conejo_placeholder
                        else -> R.drawable.animal_placeholder
                    }

                    Image(
                        painter = painterResource(id = drawableRes),
                        contentDescription = animal.name,
                        modifier = Modifier.size(100.dp)
                    )
                }
            }
        }
    }
}
