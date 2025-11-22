package com.example.amilimetros.ui.screen

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.amilimetros.data.local.adoption.AdoptionFormEntity
import com.example.amilimetros.data.local.database.AppDatabase
import com.example.amilimetros.ui.notification.NotificationManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AdoptionFormScreen(
    animalId: Long,
    animalName: String,
    userId: Long,
    userName: String,
    userEmail: String,
    userPhone: String,
    onSubmitSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { AppDatabase.getInstance(context) }

    // Estados del formulario
    var reason by remember { mutableStateOf("") }
    var livesInApartment by remember { mutableStateOf(false) }
    var hasBalconyNets by remember { mutableStateOf(false) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var photoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // Permisos
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES
        )
    )

    // Launcher para galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            photoUri = it
            scope.launch {
                photoBitmap = loadBitmapFromUri(context, it)
            }
        }
    }

    // Launcher para cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            scope.launch {
                photoBitmap = loadBitmapFromUri(context, photoUri!!)
            }
        } else {
            photoUri = null
            photoBitmap = null
        }
    }

    // Función para crear archivo temporal para la foto
    fun createImageFile(): Uri {
        val storageDir = context.getExternalFilesDir(null)
        val imageFile = File.createTempFile(
            "adoption_photo_${System.currentTimeMillis()}",
            ".jpg",
            storageDir
        )
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Formulario de Adopción") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
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
            // ========== HEADER ==========
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Filled.Pets,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Adoptar a $animalName",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Completa este formulario para solicitar la adopción",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ========== DATOS DEL USUARIO ==========
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tus Datos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Nombre: $userName")
                    Text("Email: $userEmail")
                    Text("Teléfono: $userPhone")
                }
            }

            // ========== MOTIVO DE ADOPCIÓN ==========
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("¿Por qué deseas adoptar a $animalName?") },
                placeholder = { Text("Cuéntanos tu motivación...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                maxLines = 6
            )

            // ========== TIPO DE VIVIENDA ==========
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Información de Vivienda",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("¿Vives en departamento?")
                        Switch(
                            checked = livesInApartment,
                            onCheckedChange = { livesInApartment = it }
                        )
                    }

                    if (livesInApartment) {
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("¿Tienes mallas en ventanas/balcón?")
                                Text(
                                    "Requerido para la seguridad del animal",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            Switch(
                                checked = hasBalconyNets,
                                onCheckedChange = { hasBalconyNets = it }
                            )
                        }

                        if (!hasBalconyNets) {
                            Spacer(Modifier.height(8.dp))
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Warning,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        "Es obligatorio tener mallas de seguridad para adoptar",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ========== FOTO DE EVIDENCIA ==========
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.CameraAlt, contentDescription = null)
                        Text(
                            text = "Foto de tu hogar",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Sube una foto de tu hogar o las mallas de seguridad (si vives en departamento)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(12.dp))

                    // Preview de la foto
                    if (photoBitmap != null) {
                        Image(
                            bitmap = photoBitmap!!.asImageBitmap(),
                            contentDescription = "Foto seleccionada",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                    } else if (photoUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(photoUri),
                            contentDescription = "Foto seleccionada",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (permissionsState.allPermissionsGranted) {
                                    galleryLauncher.launch("image/*")
                                } else {
                                    permissionsState.launchMultiplePermissionRequest()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.PhotoLibrary, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("Galería")
                        }

                        Button(
                            onClick = {
                                if (permissionsState.allPermissionsGranted) {
                                    val uri = createImageFile()
                                    photoUri = uri
                                    cameraLauncher.launch(uri)
                                } else {
                                    permissionsState.launchMultiplePermissionRequest()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.CameraAlt, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("Cámara")
                        }
                    }
                }
            }

            // ========== MENSAJE DE ERROR ==========
            if (errorMsg != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Filled.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = errorMsg!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

// ========== BOTÓN ENVIAR (LÍNEAS 387-450) - REEMPLAZAR COMPLETAMENTE ==========
            Button(
                onClick = {
                    // Validaciones
                    when {
                        reason.isBlank() -> {
                            errorMsg = "Debes escribir el motivo de adopción"
                        }
                        reason.length < 20 -> {
                            errorMsg = "El motivo debe tener al menos 20 caracteres"
                        }
                        livesInApartment && !hasBalconyNets -> {
                            errorMsg = "Debes tener mallas de seguridad para adoptar viviendo en departamento"
                        }
                        photoBitmap == null && photoUri == null -> {
                            errorMsg = "Debes subir una foto de tu hogar"
                        }
                        else -> {
                            errorMsg = null
                            isSubmitting = true

                            scope.launch {
                                try {
                                    // ✅ CORREGIDO: Validar que bitmap no sea null
                                    val bitmap = photoBitmap ?: withContext(Dispatchers.IO) {
                                        loadBitmapFromUri(context, photoUri!!)
                                    }

                                    if (bitmap == null) {
                                        errorMsg = "Error al cargar la imagen"
                                        isSubmitting = false
                                        NotificationManager.showError("Error al cargar la imagen")
                                        return@launch
                                    }

                                    // ✅ Convertir foto a ByteArray (ahora bitmap es no-nullable)
                                    val photoBytes = withContext(Dispatchers.IO) {
                                        bitmapToByteArray(bitmap)
                                    }

                                    // ✅ Crear formulario con foto en ByteArray
                                    val form = AdoptionFormEntity(
                                        userId = userId,
                                        animalId = animalId,
                                        animalName = animalName,
                                        userName = userName,
                                        userEmail = userEmail,
                                        userPhone = userPhone,
                                        reason = reason.trim(),
                                        hasBalconyNets = hasBalconyNets,
                                        livesInApartment = livesInApartment,
                                        photo = photoBytes,  // ✅ ByteArray
                                        status = "Pendiente"
                                    )

                                    // ✅ Guardar en BD
                                    withContext(Dispatchers.IO) {
                                        db.adoptionFormDao().insert(form)
                                    }

                                    isSubmitting = false
                                    NotificationManager.showSuccess("¡Solicitud de adopción enviada con éxito!")
                                    onSubmitSuccess()

                                } catch (e: Exception) {
                                    isSubmitting = false
                                    errorMsg = "Error al enviar la solicitud: ${e.message}"
                                    NotificationManager.showError("Error al enviar la solicitud")
                                }
                            }
                        }
                    }
                },
                enabled = !isSubmitting,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Enviando...")
                } else {
                    Icon(Icons.Filled.Send, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Enviar Solicitud de Adopción")
                }
            }
            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }
}

// ✅ FUNCIONES AUXILIARES PARA MANEJAR IMÁGENES
private suspend fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            null
        }
    }
}

private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
    return outputStream.toByteArray()
}