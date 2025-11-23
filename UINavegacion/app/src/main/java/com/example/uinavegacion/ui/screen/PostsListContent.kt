package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.layout.* // Layouts
import androidx.compose.foundation.lazy.LazyColumn // Lista perezosa
import androidx.compose.foundation.lazy.items // Items de lista
import androidx.compose.material.icons.Icons // Íconos
import androidx.compose.material.icons.filled.Delete // Ícono eliminar
import androidx.compose.material.icons.filled.Edit // Ícono editar
import androidx.compose.material.icons.filled.Refresh // Ícono refrescar
import androidx.compose.material3.* // Material 3
import androidx.compose.runtime.* // Estado Compose
import androidx.compose.ui.Modifier // Modificadores
import androidx.compose.ui.text.font.FontWeight // Estilo de texto
import androidx.compose.ui.unit.dp // Tamaños
import com.example.uinavegacion.ui.viewmodel.PostViewModel // VM de posts
import com.example.uinavegacion.data.remote.dto.PostDto // DTO de post

@Composable
fun PostsListContent(
    vm: PostViewModel,            // VM para leer estado y ejecutar acciones
    onAdd: () -> Unit,            // Navegar a agregar
    onEdit: (Int) -> Unit         // Navegar a editar con id
) {
    // Leemos el estado actual
    val state = vm.uiState

    // Cargamos al entrar
    LaunchedEffect(Unit) { vm.loadPosts() }

    // Host para snackbars
    val snackbarHostState = remember { SnackbarHostState() }
    // Si hay mensaje de acción, lo mostramos
    LaunchedEffect(state.lastActionMessage) {
        state.lastActionMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    // Encabezado con acciones (NO Scaffold: esto es contenido puro)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Posts", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        IconButton(onClick = { vm.loadPosts() }, enabled = !state.isLoading) {
            Icon(Icons.Filled.Refresh, contentDescription = "Refrescar")
        }
    }

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

    // Botón agregar que tu NavGraph puede complementar con un FAB global si quieres
    OutlinedButton(onClick = onAdd) {
        Text("Agregar nuevo")
    }

    Spacer(Modifier.height(8.dp))

    // Lista con scroll correcto
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
        items(state.posts) { post ->
            PostListItem(
                post = post,
                onEdit = { onEdit(post.id) },
                onDelete = { vm.deletePost(post.id) }
            )
        }
    }
}

// Ítem reutilizable con acciones Editar/Eliminar
@Composable
private fun PostListItem(
    post: PostDto,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text("(${post.id}) ${post.title}", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(post.body)
            Spacer(Modifier.height(8.dp))
            Row {
                OutlinedButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Editar")
                }
                Spacer(Modifier.width(8.dp))
                OutlinedButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Eliminar")
                }
            }
        }
    }
}