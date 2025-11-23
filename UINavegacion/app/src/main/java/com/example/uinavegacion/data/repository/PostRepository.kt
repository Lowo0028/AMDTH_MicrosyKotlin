package com.example.uinavegacion.data.repository

// Importamos la interfaz de la API y el DTO de datos.
import com.example.uinavegacion.data.remote.JsonPlaceholderApi
import com.example.uinavegacion.data.remote.RemoteModule
import com.example.uinavegacion.data.remote.dto.PostDto
import retrofit2.HttpException

// El repositorio se encarga de comunicarse con la API y manejar errores.
class PostRepository(
    private val api: JsonPlaceholderApi = RemoteModule.create(JsonPlaceholderApi::class.java)
) {
    // Obtiene todos los posts desde la API.
    suspend fun fetchPosts(): Result<List<PostDto>> = try {
        Result.success(api.getPosts())
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Obtiene un post específico por su ID.
    suspend fun fetchPostById(id: Int): Result<PostDto> = try {
        Result.success(api.getPostById(id))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Crea un nuevo post.
    suspend fun create(post: PostDto): Result<PostDto> = try {
        Result.success(api.createPost(post))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Actualiza un post existente.
    suspend fun update(id: Int, post: PostDto): Result<PostDto> = try {
        Result.success(api.updatePost(id, post))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Elimina un post por su ID.
    suspend fun delete(id: Int): Result<Unit> = try {
        val resp = api.deletePost(id)
        if (resp.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(HttpException(resp))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}