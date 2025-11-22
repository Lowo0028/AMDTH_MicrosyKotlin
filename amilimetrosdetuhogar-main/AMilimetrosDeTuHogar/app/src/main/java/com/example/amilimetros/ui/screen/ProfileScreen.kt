package com.example.amilimetros.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.amilimetros.data.local.database.AppDatabase
import com.example.amilimetros.data.local.storage.UserPreferences
import com.example.amilimetros.data.repository.UserRepository
import com.example.amilimetros.ui.notification.NotificationManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPrefs = remember { UserPreferences(context) }
    val db = remember { AppDatabase.getInstance(context) }
    val userRepo = remember { UserRepository(db.userDao()) }

    val userId by userPrefs.userId.collectAsStateWithLifecycle(0L)
    val userName by userPrefs.userName.collectAsStateWithLifecycle("")
    val userEmail by userPrefs.userEmail.collectAsStateWithLifecycle("")
    val userPhone by userPrefs.userPhone.collectAsStateWithLifecycle("")

    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ========== INFORMACIÓN DEL USUARIO ==========
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(
                                    Icons.Filled.Email,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = userEmail,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Spacer(Modifier.height(2.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(
                                    Icons.Filled.Phone,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = userPhone,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            // ========== OPCIONES ==========
            Text(
                text = "Configuración de Cuenta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = { showEditProfileDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Edit, null)
                Spacer(Modifier.width(8.dp))
                Text("Editar Perfil")
            }

            OutlinedButton(
                onClick = { showChangePasswordDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Lock, null)
                Spacer(Modifier.width(8.dp))
                Text("Cambiar Contraseña")
            }

            HorizontalDivider()

            OutlinedButton(
                onClick = {
                    scope.launch {
                        userPrefs.logout()
                        NotificationManager.showInfo("Sesión cerrada")
                        onLogout()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Filled.Logout, null)
                Spacer(Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }
        }
    }

    // ========== DIÁLOGO CAMBIAR CONTRASEÑA ==========
    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onConfirm = { oldPass, newPass ->
                scope.launch {
                    val result = userRepo.changePassword(userId, oldPass, newPass)
                    if (result.isSuccess) {
                        NotificationManager.showSuccess("Contraseña actualizada correctamente")
                        showChangePasswordDialog = false
                    } else {
                        NotificationManager.showError(result.exceptionOrNull()?.message ?: "Error al cambiar contraseña")
                    }
                }
            }
        )
    }

    // ========== DIÁLOGO EDITAR PERFIL ==========
    if (showEditProfileDialog) {
        EditProfileDialog(
            currentName = userName,
            currentPhone = userPhone,
            onDismiss = { showEditProfileDialog = false },
            onConfirm = { newName, newPhone ->
                scope.launch {
                    val user = userRepo.getUserById(userId).getOrNull()
                    if (user != null) {
                        val updatedUser = user.copy(
                            name = newName,
                            phone = newPhone
                        )
                        val result = userRepo.updateUser(updatedUser)
                        if (result.isSuccess) {
                            userPrefs.setUserName(newName)
                            userPrefs.setUserPhone(newPhone)
                            NotificationManager.showSuccess("Perfil actualizado correctamente")
                            showEditProfileDialog = false
                        } else {
                            NotificationManager.showError("Error al actualizar perfil")
                        }
                    }
                }
            }
        )
    }
}

// ========== DIÁLOGO CAMBIAR CONTRASEÑA ==========
@Composable
private fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Filled.Lock, null, tint = MaterialTheme.colorScheme.primary) },
        title = { Text("Cambiar Contraseña") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text("Contraseña actual") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nueva contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar nueva") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (showError != null) {
                    Text(
                        text = showError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        oldPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank() -> {
                            showError = "Completa todos los campos"
                        }
                        newPassword != confirmPassword -> {
                            showError = "Las contraseñas no coinciden"
                        }
                        newPassword.length < 8 -> {
                            showError = "La contraseña debe tener al menos 8 caracteres"
                        }
                        else -> {
                            showError = null
                            onConfirm(oldPassword, newPassword)
                        }
                    }
                }
            ) {
                Text("Cambiar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// ========== DIÁLOGO EDITAR PERFIL ==========
@Composable
private fun EditProfileDialog(
    currentName: String,
    currentPhone: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var phone by remember { mutableStateOf(currentPhone) }
    var showError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Filled.Edit, null, tint = MaterialTheme.colorScheme.primary) },
        title = { Text("Editar Perfil") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.Person, null) }
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.Phone, null) }
                )

                if (showError != null) {
                    Text(
                        text = showError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        name.isBlank() -> {
                            showError = "El nombre no puede estar vacío"
                        }
                        phone.isBlank() -> {
                            showError = "El teléfono no puede estar vacío"
                        }
                        else -> {
                            showError = null
                            onConfirm(name.trim(), phone.trim())
                        }
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}