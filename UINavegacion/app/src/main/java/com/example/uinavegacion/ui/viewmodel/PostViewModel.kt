package com.example.uinavegacion.ui.viewmodel

// Importamos las librerías necesarias para ViewModel y Compose.
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch

// Importamos nuestro modelo de datos y el repositorio.
import com.example.uinavegacion.data.remote.dto.PostDto
import com.example.uinavegacion.data.repository.PostRepository

// Clase de estado que contiene toda la información que la UI necesita observar.
data class PostsUiState(
    val isLoading: Boolean = false,      // Indica si la app está cargando datos.
    val posts: List<PostDto> = emptyList(), // Lista actual de posts mostrados en pantalla.
    val error: String? = null,           // Mensaje de error, si ocurre alguno.
    val lastActionMessage: String? = null // Mensaje de retroalimentación al realizar CRUD.
)

// ViewModel que maneja la lógica de negocio y actualiza el estado para la UI.
class PostViewModel(
    private val repository: PostRepository = PostRepository()
) : ViewModel() {

    // Estado observable que Compose utilizará para actualizar la interfaz automáticamente.
    var uiState by mutableStateOf(PostsUiState())
        private set

    // Obtiene todos los posts desde la API.
    fun loadPosts() {
        uiState = uiState.copy(isLoading = true, error = null, lastActionMessage = null)
        viewModelScope.launch {
            val result = repository.fetchPosts()
            uiState = result.fold(
                onSuccess = { data ->
                    uiState.copy(isLoading = false, posts = data)
                },
                onFailure = { e ->
                    uiState.copy(isLoading = false, error = e.message ?: "Error desconocido")
                }
            )
        }
    }

    // Obtiene un post específico por su ID.
    fun loadPostById(id: Int) {
        uiState = uiState.copy(isLoading = true, error = null, lastActionMessage = null)
        viewModelScope.launch {
            val result = repository.fetchPostById(id)
            uiState = result.fold(
                onSuccess = { post ->
                    uiState.copy(
                        isLoading = false,
                        posts = listOf(post),
                        lastActionMessage = "Cargado id=$id"
                    )
                },
                onFailure = { e ->
                    uiState.copy(isLoading = false, error = e.message ?: "Error desconocido")
                }
            )
        }
    }

    // Crea un nuevo post y actualiza la lista localmente.
    fun createPost(sample: PostDto) {
        uiState = uiState.copy(isLoading = true, error = null, lastActionMessage = null)
        viewModelScope.launch {
            val result = repository.create(sample)
            uiState = result.fold(
                onSuccess = { created ->
                    val updatedList = uiState.posts + created
                    uiState.copy(
                        isLoading = false,
                        posts = updatedList,
                        lastActionMessage = "Creado id=${created.id}"
                    )
                },
                onFailure = { e ->
                    uiState.copy(isLoading = false, error = e.message ?: "Error al crear")
                }
            )
        }
    }

    // Actualiza un post y refresca la lista localmente.
    fun updatePost(id: Int, updated: PostDto) {
        uiState = uiState.copy(isLoading = true, error = null, lastActionMessage = null)
        viewModelScope.launch {
            val result = repository.update(id, updated)
            uiState = result.fold(
                onSuccess = { post ->
                    val newList = uiState.posts.map { if (it.id == id) post else it }
                    uiState.copy(
                        isLoading = false,
                        posts = newList,
                        lastActionMessage = "Actualizado id=$id"
                    )
                },
                onFailure = { e ->
                    uiState.copy(isLoading = false, error = e.message ?: "Error al actualizar")
                }
            )
        }
    }

    // Elimina un post de la lista localmente para mostrar el cambio inmediato.
    fun deletePost(id: Int) {
        uiState = uiState.copy(isLoading = true, error = null, lastActionMessage = null)
        viewModelScope.launch {
            val result = repository.delete(id)
            uiState = result.fold(
                onSuccess = {
                    val newList = uiState.posts.filterNot { it.id == id }
                    uiState.copy(
                        isLoading = false,
                        posts = newList,
                        lastActionMessage = "Eliminado id=$id"
                    )
                },
                onFailure = { e ->
                    uiState.copy(isLoading = false, error = e.message ?: "Error al eliminar")
                }
            )
        }
    }
}