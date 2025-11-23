
package com.example.amilimetros.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.example.amilimetros.data.local.storage.UserPreferences
import com.example.amilimetros.data.repository.AuthApiRepository
import com.example.amilimetros.ui.viewmodel.AuthViewModel
import com.example.amilimetros.ui.viewmodel.RegisterState

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val authRepository = remember { AuthApiRepository() }
    val viewModel = remember { AuthViewModel(authRepository, userPrefs) }

    val registerState by viewModel.registerState.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showPassword by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    var nombreError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var telefonoError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(registerState) {
        if (registerState is RegisterState.Success) {
            viewModel.resetStates()
            onNavigateToLogin()
        }
    }

    val bg = MaterialTheme.colorScheme.tertiaryContainer

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Text("Crear Cuenta", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text("Completa tus datos para registrarte", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(20.dp))

            // NOMBRE
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    nombreError = if (it.isBlank()) "El nombre es requerido" else null
                },
                label = { Text("Nombre completo") },
                singleLine = true,
                isError = nombreError != null,
                modifier = Modifier.fillMaxWidth()
            )
            if (nombreError != null) {
                Text(nombreError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(12.dp))

            // EMAIL
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = if (it.isBlank()) "El email es requerido" else null
                },
                label = { Text("Email") },
                singleLine = true,
                isError = emailError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            if (emailError != null) {
                Text(emailError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(12.dp))

            // TELÉFONO
            OutlinedTextField(
                value = telefono,
                onValueChange = {
                    telefono = it
                    telefonoError = if (it.isBlank()) "El teléfono es requerido" else null
                },
                label = { Text("Teléfono") },
                singleLine = true,
                isError = telefonoError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            if (telefonoError != null) {
                Text(telefonoError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(12.dp))

            // PASSWORD
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = when {
                        it.isBlank() -> "La contraseña es requerida"
                        it.length < 6 -> "Mínimo 6 caracteres"
                        else -> null
                    }
                },
                label = { Text("Contraseña") },
                singleLine = true,
                isError = passwordError != null,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            if (passwordError != null) {
                Text(passwordError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(12.dp))

            // CONFIRMAR
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmError = if (it != password) "Las contraseñas no coinciden" else null
                },
                label = { Text("Confirmar contraseña") },
                singleLine = true,
                isError = confirmError != null,
                visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showConfirm = !showConfirm }) {
                        Icon(if (showConfirm) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            if (confirmError != null) {
                Text(confirmError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(20.dp))

            // BOTÓN REGISTRAR
            Button(
                onClick = {
                    // Validaciones
                    var hasErrors = false
                    if (nombre.isBlank()) { nombreError = "El nombre es requerido"; hasErrors = true }
                    if (email.isBlank()) { emailError = "El email es requerido"; hasErrors = true }
                    if (telefono.isBlank()) { telefonoError = "El teléfono es requerido"; hasErrors = true }
                    if (password.length < 6) { passwordError = "Mínimo 6 caracteres"; hasErrors = true }
                    if (password != confirmPassword) { confirmError = "Las contraseñas no coinciden"; hasErrors = true }

                    if (!hasErrors) {
                        viewModel.register(nombre, email, telefono, password)
                    }
                },
                enabled = registerState !is RegisterState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (registerState is RegisterState.Loading) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Creando cuenta...")
                } else {
                    Text("Registrar")
                }
            }

            if (registerState is RegisterState.Error) {
                Spacer(Modifier.height(12.dp))
                Text((registerState as RegisterState.Error).message, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(onClick = onNavigateToLogin, modifier = Modifier.fillMaxWidth()) {
                Text("Ya tengo cuenta")
            }
        }
    }
}