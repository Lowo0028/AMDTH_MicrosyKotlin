package com.example.uinavegacion.ui.screen

// Importaciones necesarias para la UI y Material 3.
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

// Importamos nuestro ViewModel y DTO.
import com.example.uinavegacion.ui.viewmodel.PostViewModel
import com.example.uinavegacion.data.remote.dto.PostDto

// Importamos el formulario reutilizable.
import com.example.uinavegacion.ui.components.PostForm
import com.example.uinavegacion.ui.components.PostFormData

@Composable
fun PostsScreen(
    vm: PostViewModel = viewModel()
) {
    // Obtenemos el estado actual del ViewModel.
    val state = vm.uiState

    // Ejecutamos la carga inicial de posts.
    LaunchedEffect(Unit) {
        vm.loadPosts()
    }

    // Variables locales para los inputs de ID.
    var getIdText by remember { mutableStateOf("") }
    var deleteIdText by remember { mutableStateOf("") }

    // Snackbar para mostrar mensajes de acción.
    val snackbarHostState = remember { SnackbarHostState() }
    val lastActionMessage = state.lastActionMessage

    // Cada vez que cambie el mensaje, mostramos el snackbar.
    LaunchedEffect(lastActionMessage) {
        lastActionMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    // Estructura base con Scaffold y SnackbarHost.
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            Text(
                text = "Posts (JSONPlaceholder) - CRUD completo",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            state.error?.let { msg ->
                Spacer(Modifier.height(8.dp))
                ElevatedCard {
                    Text(
                        text = "Error: $msg",
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // -------------------------
            // GET /posts/{id}
            // -------------------------
            Text("GET /posts/{id}", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = getIdText,
                    onValueChange = { getIdText = it },
                    label = { Text("ID a consultar") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        val id = getIdText.toIntOrNull()
                        if (id != null) vm.loadPostById(id)
                    },
                    enabled = !state.isLoading
                ) {
                    Text("Consultar")
                }
            }

            Spacer(Modifier.height(16.dp))

            // -------------------------
            // POST /posts
            // -------------------------
            Text("POST /posts", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            PostForm(
                submitLabel = "Crear post",
                onSubmit = { form ->
                    val dto = PostDto(
                        userId = form.userId,
                        id = form.id ?: 0,
                        title = form.title,
                        body = form.body
                    )
                    vm.createPost(dto)
                }
            )

            Spacer(Modifier.height(16.dp))

            // -------------------------
            // PUT /posts/{id}
            // -------------------------
            Text("PUT /posts/{id}", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            PostForm(
                submitLabel = "Actualizar post",
                onSubmit = { form ->
                    val id = form.id ?: return@PostForm
                    val dto = PostDto(
                        userId = form.userId,
                        id = id,
                        title = form.title,
                        body = form.body
                    )
                    vm.updatePost(id, dto)
                }
            )

            Spacer(Modifier.height(16.dp))

            // -------------------------
            // DELETE /posts/{id}
            // -------------------------
            Text("DELETE /posts/{id}", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = deleteIdText,
                    onValueChange = { deleteIdText = it },
                    label = { Text("ID a eliminar") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        val id = deleteIdText.toIntOrNull()
                        if (id != null) vm.deletePost(id)
                    },
                    enabled = !state.isLoading
                ) {
                    Text("Eliminar")
                }
            }

            Spacer(Modifier.height(18.dp))

            // -------------------------
            // Lista de posts
            // -------------------------
            if (state.posts.isNotEmpty()) {
                Text("Resultados", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.posts) { post ->
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp)) {
                                Text("(${post.id}) ${post.title}", fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(4.dp))
                                Text(post.body)
                            }
                        }
                    }
                }
            }
        }
    }
}