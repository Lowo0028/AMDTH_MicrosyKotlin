package com.example.amilimetros.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.amilimetros.data.local.storage.UserPreferences

@Composable
fun HomeScreen(
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit,
    onGoProducts: () -> Unit,
    onGoAnimals: () -> Unit,
    onGoCart: () -> Unit,
    onGoAdmin: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }

    val isLoggedIn by userPrefs.isLoggedIn.collectAsStateWithLifecycle(false)
    val isAdmin by userPrefs.isAdmin.collectAsStateWithLifecycle(false)

    val bg = MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ========== HEADER ==========
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🐾 A Milimetros de tu hogar",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    imageVector = if (isLoggedIn) Icons.Filled.Person else Icons.Filled.PersonOff,
                    contentDescription = if (isLoggedIn) "Usuario Logueado" else "Usuario no Logueado",
                    tint = if (isLoggedIn) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline
                )
            }

            // ========== BIENVENIDA ==========
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Bienvenido a nuestra tienda de mascotas",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Encuentra productos para tus mascotas y adopta animales en busca de hogar",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // ========== BOTONES PRINCIPALES ==========
            if (isLoggedIn) {
                // Usuario logueado
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "¿Qué deseas hacer?",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Button(
                            onClick = onGoProducts,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.ShoppingCart, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Ver Productos")
                        }

                        Button(
                            onClick = onGoAnimals,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.Pets, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Animales en Adopción")
                        }

                        OutlinedButton(
                            onClick = onGoCart,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.ShoppingBag, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Mi Carrito")
                        }

                        // ========== BOTÓN ADMIN ==========
                        if (isAdmin) {
                            Divider()
                            Button(
                                onClick = onGoAdmin,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary
                                )
                            ) {
                                Icon(Icons.Filled.AdminPanelSettings, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Panel de Administrador")
                            }
                        }

                        Divider()

                        // ========== BOTÓN LOGOUT ==========
                        OutlinedButton(
                            onClick = onLogout,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Filled.Logout, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Cerrar Sesión")
                        }
                    }
                }
            } else {
                // Usuario NO logueado
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Para acceder a todas las funciones",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )

                        Button(
                            onClick = onGoLogin,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.Login, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Iniciar Sesión")
                        }

                        OutlinedButton(
                            onClick = onGoRegister,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.PersonAdd, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Crear Cuenta")
                        }
                    }
                }
            }
        }
    }
}