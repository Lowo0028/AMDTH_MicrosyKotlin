package com.example.uinavegacion.data.repository

import com.example.uinavegacion.data.remote.JsonPlaceholderApi
import com.example.uinavegacion.data.remote.dto.PostDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PostsRepositoryTest {
    @Test
    fun fetchPosts_devuelve_api_ok() = runBlocking{
        val api = mockk<JsonPlaceholderApi>()
        val repo = PostRepository(api)
        val sample = listOf(PostDto(1,1,"Hola Mundo","Soy una publicacion nueva"))

        coEvery { api.getPosts() } returns sample

        val result = repo.fetchPosts()
        assertTrue(result.isSuccess) //test para comunicacion exitosa
        assertEquals(1,result.getOrNull()!!.size) //tamaño sea correcto
        assertEquals("Hola Mundo",result.getOrNull()!![0].title) //titulo retornado coincida


    }
}