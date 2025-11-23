package com.example.uinavegacion.ui.components

// Importamos los componentes de Jetpack Compose.
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp

// Data class que representa los datos ingresados en el formulario.
data class PostFormData(
    val userId: Int,
    val id: Int?,
    val title: String,
    val body: String
)

// Composable reutilizable para ingresar datos de un Post.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostForm(
    modifier: Modifier = Modifier,
    initial: PostFormData = PostFormData(userId = 1, id = null, title = "", body = ""),
    submitLabel: String,
    onSubmit: (PostFormData) -> Unit
) {
    // Estados locales para los campos del formulario.
    var userIdText by remember { mutableStateOf(initial.userId.toString()) }
    var idText by remember { mutableStateOf(initial.id?.toString() ?: "") }
    var title by remember { mutableStateOf(initial.title) }
    var body by remember { mutableStateOf(initial.body) }

    // Diseño del formulario.
    Column(modifier = modifier) {

        // Campo para User ID.
        OutlinedTextField(
            value = userIdText,
            onValueChange = { userIdText = it },
            label = { Text("User ID (número)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            )
        )

        Spacer(Modifier.height(8.dp))

        // Campo para ID (usado en PUT y DELETE).
        OutlinedTextField(
            value = idText,
            onValueChange = { idText = it },
            label = { Text("ID (solo para actualizar)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            )
        )

        Spacer(Modifier.height(8.dp))

        // Campo para título del post.
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )

        Spacer(Modifier.height(8.dp))

        // Campo para el contenido del post.
        OutlinedTextField(
            value = body,
            onValueChange = { body = it },
            label = { Text("Contenido") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        )

        Spacer(Modifier.height(12.dp))

        // Botón para enviar los datos al callback.
        Button(
            onClick = {
                val userId = userIdText.toIntOrNull() ?: 1
                val id = idText.toIntOrNull()
                onSubmit(
                    PostFormData(
                        userId = userId,
                        id = id,
                        title = title.trim(),
                        body = body.trim()
                    )
                )
            }
        ) {
            Text(submitLabel)
        }
    }
}