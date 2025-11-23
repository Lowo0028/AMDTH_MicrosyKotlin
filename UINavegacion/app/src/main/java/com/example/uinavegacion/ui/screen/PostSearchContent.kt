package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.layout.* // Layouts
import androidx.compose.foundation.rememberScrollState // Estado de scroll
import androidx.compose.foundation.verticalScroll // Scroll vertical
import androidx.compose.material3.* // Material 3
import androidx.compose.runtime.* // Estado Compose
import androidx.compose.ui.Modifier // Modificadores
import androidx.compose.ui.text.font.FontWeight // Estilo
import androidx.compose.ui.unit.dp // Tamaños
import com.example.uinavegacion.ui.viewmodel.PostViewModel // VM de posts

@Composable
fun PostSearchContent(
    vm: PostViewModel // VM para buscar
) {
    // Leemos el estado del VM
    val state = vm.uiState

    // Campo local para el ID
    var idText by remember { mutableStateOf("") }

    // Scroll vertical para asegurar visibilidad
    val scroll = rememberScrollState()

    // Título de sección
    Text("Buscar por ID", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(24.dp))
    // Indicador de carga
    if (state.isLoading) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
    }

    // Error si existe
    state.error?.let { msg ->
        ElevatedCard {
            Text(text = "Error: $msg", modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(8.dp))
    }
    Spacer(Modifier.width(24.dp))
    // Contenedor con scroll para inputs y resultado
    Column(modifier = Modifier.verticalScroll(scroll).padding(top = 24.dp)) {
        Row(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = idText,
                onValueChange = { idText = it },
                label = { Text("ID a consultar") },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = { idText.toIntOrNull()?.let { vm.loadPostById(it) } },
                enabled = !state.isLoading
            ) { Text("Consultar") }
        }

        Spacer(Modifier.height(16.dp))

        if (state.posts.isNotEmpty()) {
            val post = state.posts.first()
            ElevatedCard {
                Column(Modifier.padding(12.dp)) {
                    Text("(${post.id}) ${post.title}", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text(post.body)
                }
            }
        }
    }
}