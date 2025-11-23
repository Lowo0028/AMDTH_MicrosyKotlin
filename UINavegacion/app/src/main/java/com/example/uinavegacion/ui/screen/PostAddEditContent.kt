package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.layout.* // Layouts
import androidx.compose.foundation.rememberScrollState // Estado de scroll
import androidx.compose.foundation.verticalScroll // Scroll vertical
import androidx.compose.material3.* // Material 3
import androidx.compose.runtime.* // Estado Compose
import androidx.compose.ui.Modifier // Modificadores
import androidx.compose.ui.unit.dp // Tamaños
import com.example.uinavegacion.ui.viewmodel.PostViewModel // VM de posts
import com.example.uinavegacion.data.remote.dto.PostDto // DTO de post
import com.example.uinavegacion.ui.components.PostForm // Formulario reutilizable
import com.example.uinavegacion.ui.components.PostFormData // Datos del formulario

@Composable
fun PostAddEditContent(
    vm: PostViewModel,        // VM para CRUD
    postId: Int?,             // null = agregar; valor = editar
    onSaved: () -> Unit,      // Vuelve a lista después de guardar
    onCancel: () -> Unit      // Vuelve sin guardar
) {
    // Leemos el estado
    val state = vm.uiState

    // Si hay postId, cargamos ese post para prellenar
    LaunchedEffect(postId) { if (postId != null) vm.loadPostById(postId) }

    // Scroll vertical para evitar superposición
    val scroll = rememberScrollState()

    // Si estamos editando, tomamos el primero del estado
    val editingPost = if (postId != null && state.posts.isNotEmpty()) state.posts.firstOrNull() else null

    // Encabezado simple sin Scaffold
    Text(text = if (postId == null) "Agregar Post" else "Editar Post", style = MaterialTheme.typography.titleLarge)

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

    // Valores iniciales del formulario según caso
    val initial = if (editingPost != null)
        PostFormData(userId = editingPost.userId, id = editingPost.id, title = editingPost.title, body = editingPost.body)
    else
        PostFormData(userId = 1, id = null, title = "", body = "")

    // Contenedor con scroll para el form
    Column(modifier = Modifier.verticalScroll(scroll).padding(top = 36.dp)) {
        PostForm(
            initial = initial,
            submitLabel = if (postId == null) "Crear" else "Actualizar",
            onSubmit = { form ->
                if (postId == null) {
                    val dto = PostDto(userId = form.userId, id = form.id ?: 0, title = form.title, body = form.body)
                    vm.createPost(dto)
                } else {
                    val id = form.id ?: postId
                    val dto = PostDto(userId = form.userId, id = id, title = form.title, body = form.body)
                    vm.updatePost(id, dto)
                }
                onSaved()
            }
        )
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onCancel) { Text("Cancelar") }
    }
}