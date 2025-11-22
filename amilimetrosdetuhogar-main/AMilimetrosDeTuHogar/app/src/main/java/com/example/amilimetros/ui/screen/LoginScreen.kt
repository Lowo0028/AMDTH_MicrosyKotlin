package com.example.amilimetros.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.amilimetros.data.local.database.AppDatabase
import com.example.amilimetros.data.local.storage.UserPreferences
import com.example.amilimetros.data.repository.UserRepository
import com.example.amilimetros.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

// ========== VERSIÓN CON VIEWMODEL ==========
@Composable
fun LoginScreenVm(
    vm: AuthViewModel,
    onLoginOkNavigateHome: () -> Unit,
    onGoRegister: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPrefs = remember { UserPreferences(context) }
    val state by vm.login.collectAsStateWithLifecycle()

    // ✅ Crear repositorio para obtener datos del usuario
    val db = remember { AppDatabase.getInstance(context) }
    val userRepo = remember { UserRepository(db.userDao()) }

    // ✅ NUEVO: Resetear formulario al salir de la pantalla
    DisposableEffect(Unit) {
        onDispose {
            vm.resetLoginForm()
        }
    }

    LaunchedEffect(state.success) {
        if (state.success) {
            scope.launch {
                // ✅ Obtener datos completos del usuario
                val userId = state.loggedUserId ?: 0L
                val user = userRepo.getUserById(userId).getOrNull()

                // Guardar sesión en DataStore
                userPrefs.setLoggedIn(true)
                userPrefs.setUserId(userId)
                userPrefs.setIsAdmin(state.isAdmin)

                // ✅ Guardar datos adicionales si el usuario existe
                user?.let {
                    userPrefs.setUserName(it.name)
                    userPrefs.setUserEmail(it.email)
                    userPrefs.setUserPhone(it.phone)
                }

                vm.clearLoginResult()
                onLoginOkNavigateHome()
            }
        }
    }

    LoginScreen(
        email = state.email,
        pass = state.pass,
        emailError = state.emailError,
        passError = state.passError,
        canSubmit = state.canSubmit,
        isSubmitting = state.isSubmitting,
        errorMsg = state.errorMsg,
        onEmailChange = vm::onLoginEmailChange,
        onPassChange = vm::onLoginPassChange,
        onSubmit = vm::submitLogin,
        onGoRegister = onGoRegister
    )
}

// ========== UI PRESENTACIONAL ==========
@Composable
private fun LoginScreen(
    email: String,
    pass: String,
    emailError: String?,
    passError: String?,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    errorMsg: String?,
    onEmailChange: (String) -> Unit,
    onPassChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onGoRegister: () -> Unit
) {
    val bg = MaterialTheme.colorScheme.secondaryContainer
    var showPass by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.height(8.dp))

            Text(
                text = "Ingresa tus credenciales",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            // ========== EMAIL ==========
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                singleLine = true,
                isError = emailError != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (emailError != null) {
                Text(
                    emailError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(Modifier.height(12.dp))

            // ========== PASSWORD ==========
            OutlinedTextField(
                value = pass,
                onValueChange = onPassChange,
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPass) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                isError = passError != null,
                modifier = Modifier.fillMaxWidth()
            )
            if (passError != null) {
                Text(
                    passError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(Modifier.height(20.dp))

            // ========== BOTÓN ENTRAR ==========
            Button(
                onClick = onSubmit,
                enabled = canSubmit && !isSubmitting,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Validando...")
                } else {
                    Text("Entrar")
                }
            }

            if (errorMsg != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    errorMsg,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(16.dp))

            // ========== BOTÓN IR A REGISTRO ==========
            OutlinedButton(
                onClick = onGoRegister,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear cuenta nueva")
            }
        }
    }
}