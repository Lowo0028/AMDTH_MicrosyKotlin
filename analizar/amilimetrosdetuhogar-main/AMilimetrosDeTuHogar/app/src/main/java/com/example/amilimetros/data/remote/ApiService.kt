package com.example.amilimetros.data.remote

import com.example.amilimetros.data.remote.dto.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface ApiService {

    // ========================================
    // AUTENTICACIÓN (Puerto 8090)
    // ========================================
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<String>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<UsuarioResponse>

    @GET("auth/usuarios/{id}")
    suspend fun getUsuarioById(@Path("id") id: Long): Response<UsuarioResponse>

    // Alias para compatibilidad
    @GET("auth/usuarios/{id}")
    suspend fun buscarUsuarioPorId(@Path("id") id: Long): Response<UsuarioResponse>

    @GET("auth/usuario/correo/{correo}")
    suspend fun getUsuarioByEmail(@Path("correo") email: String): Response<UsuarioResponse>

    // Alias para compatibilidad
    @GET("auth/usuario/correo/{correo}")
    suspend fun buscarUsuarioPorCorreo(@Path("correo") email: String): Response<UsuarioResponse>

    @GET("auth/usuarios")
    suspend fun obtenerUsuarios(): Response<List<UsuarioResponse>>

    // ========================================
    // PRODUCTOS - CATÁLOGO (Puerto 8091)
    // ========================================
    @GET("productos")
    suspend fun getProductos(): Response<List<ProductoDto>>

    // Alias
    @GET("productos")
    suspend fun obtenerProductos(): Response<List<ProductoDto>>

    @GET("productos/{id}")
    suspend fun getProductoById(@Path("id") id: Long): Response<ProductoDto>

    // Alias
    @GET("productos/{id}")
    suspend fun obtenerProductoPorId(@Path("id") id: Long): Response<ProductoDto>

    @GET("productos/categoria/{categoria}")
    suspend fun getProductosByCategoria(@Path("categoria") categoria: String): Response<List<ProductoDto>>

    // Alias
    @GET("productos/categoria/{categoria}")
    suspend fun obtenerProductosPorCategoria(@Path("categoria") categoria: String): Response<List<ProductoDto>>

    @GET("productos/buscar")
    suspend fun buscarProductos(@Query("nombre") nombre: String): Response<List<ProductoDto>>

    @POST("productos")
    suspend fun crearProducto(@Body producto: Map<String, Any>): Response<ProductoDto>

    @PUT("productos/{id}")
    suspend fun actualizarProducto(
        @Path("id") id: Long,
        @Body producto: Map<String, Any>
    ): Response<ProductoDto>

    @DELETE("productos/{id}")
    suspend fun eliminarProducto(
        @Path("id") id: Long,
        @Query("emailAdmin") emailAdmin: String
    ): Response<Unit>

    // ========================================
    // ANIMALES (Puerto 8093)
    // ========================================
    @GET("animales")
    suspend fun obtenerAnimales(): Response<List<AnimalDto>>

    @GET("animales/disponibles")
    suspend fun getAnimalesDisponibles(): Response<List<AnimalDto>>

    // Alias
    @GET("animales/disponibles")
    suspend fun obtenerAnimalesDisponibles(): Response<List<AnimalDto>>

    @GET("animales/adoptados")
    suspend fun obtenerAnimalesAdoptados(): Response<List<AnimalDto>>

    @GET("animales/{id}")
    suspend fun getAnimalById(@Path("id") id: Long): Response<AnimalDto>

    // Alias
    @GET("animales/{id}")
    suspend fun obtenerAnimalPorId(@Path("id") id: Long): Response<AnimalDto>

    @GET("animales/especie/{especie}")
    suspend fun obtenerAnimalesPorEspecie(@Path("especie") especie: String): Response<List<AnimalDto>>

    @GET("animales/buscar")
    suspend fun buscarAnimales(@Query("nombre") nombre: String): Response<List<AnimalDto>>

    @POST("animales")
    suspend fun crearAnimal(@Body animal: Map<String, Any>): Response<AnimalDto>

    @PUT("animales/{id}")
    suspend fun actualizarAnimal(
        @Path("id") id: Long,
        @Body animal: Map<String, Any>
    ): Response<AnimalDto>

    @PUT("animales/{id}/adoptar")
    suspend fun marcarAnimalComoAdoptado(
        @Path("id") id: Long,
        @Query("emailAdmin") emailAdmin: String
    ): Response<Unit>

    @DELETE("animales/{id}")
    suspend fun eliminarAnimal(
        @Path("id") id: Long,
        @Query("emailAdmin") emailAdmin: String
    ): Response<Unit>

    // ========================================
    // CARRITO (Puerto 8092)
    // ========================================
    @GET("carrito/usuario/{usuarioId}")
    suspend fun getCarritoByUser(@Path("usuarioId") usuarioId: Long): Response<List<CarritoItemDto>>

    // Alias
    @GET("carrito/usuario/{usuarioId}")
    suspend fun obtenerCarrito(@Path("usuarioId") usuarioId: Long): Response<List<CarritoItemDto>>

    @GET("carrito/usuario/{usuarioId}/detalles")
    suspend fun obtenerDetallesCarrito(@Path("usuarioId") usuarioId: Long): Response<Map<String, Any>>

    @POST("carrito/agregar")
    suspend fun addToCart(@Body request: AddToCartRequest): Response<CarritoItemDto>

    // Alias
    @POST("carrito/agregar")
    suspend fun agregarAlCarrito(@Body request: AddToCartRequest): Response<CarritoItemDto>

    @PUT("carrito/item/{itemId}")
    suspend fun updateCartItem(
        @Path("itemId") itemId: Long,
        @Body cantidad: Map<String, Int>
    ): Response<CarritoItemDto>

    // Alias
    @PUT("carrito/item/{itemId}")
    suspend fun actualizarCantidadItem(
        @Path("itemId") itemId: Long,
        @Body cantidad: Map<String, Int>
    ): Response<CarritoItemDto>

    @DELETE("carrito/item/{itemId}")
    suspend fun deleteCartItem(@Path("itemId") itemId: Long): Response<Unit>

    // Alias
    @DELETE("carrito/item/{itemId}")
    suspend fun eliminarItemCarrito(@Path("itemId") itemId: Long): Response<Unit>

    @DELETE("carrito/usuario/{usuarioId}")
    suspend fun clearCart(@Path("usuarioId") usuarioId: Long): Response<Unit>

    // Alias
    @DELETE("carrito/usuario/{usuarioId}")
    suspend fun vaciarCarrito(@Path("usuarioId") usuarioId: Long): Response<Unit>

    @GET("carrito/usuario/{usuarioId}/total")
    suspend fun getCartTotal(@Path("usuarioId") usuarioId: Long): Response<Map<String, Double>>

    // Alias
    @GET("carrito/usuario/{usuarioId}/total")
    suspend fun calcularTotalCarrito(@Path("usuarioId") usuarioId: Long): Response<Map<String, Double>>

    // ========================================
    // FORMULARIOS DE ADOPCIÓN (Puerto 8094)
    // ========================================
    @GET("formularios")
    suspend fun obtenerTodosFormularios(@Query("emailAdmin") emailAdmin: String): Response<List<FormularioAdopcionDto>>

    @GET("formularios/{id}")
    suspend fun obtenerFormularioPorId(@Path("id") id: Long): Response<FormularioAdopcionDto>

    @GET("formularios/usuario/{usuarioId}")
    suspend fun obtenerFormulariosPorUsuario(@Path("usuarioId") usuarioId: Long): Response<List<FormularioAdopcionDto>>

    @GET("formularios/animal/{animalId}")
    suspend fun obtenerFormulariosPorAnimal(@Path("animalId") animalId: Long): Response<List<FormularioAdopcionDto>>

    @GET("formularios/estado/{estado}")
    suspend fun obtenerFormulariosPorEstado(
        @Path("estado") estado: String,
        @Query("emailAdmin") emailAdmin: String
    ): Response<List<FormularioAdopcionDto>>

    @POST("formularios/adoptar/{usuarioId}/{animalId}")
    suspend fun crearFormulario(
        @Path("usuarioId") usuarioId: Long,
        @Path("animalId") animalId: Long,
        @Body formulario: Map<String, Any>
    ): Response<FormularioAdopcionDto>

    @PUT("formularios/{id}/aprobar")
    suspend fun aprobarFormulario(
        @Path("id") id: Long,
        @Body body: Map<String, String>
    ): Response<FormularioAdopcionDto>

    @PUT("formularios/{id}/rechazar")
    suspend fun rechazarFormulario(
        @Path("id") id: Long,
        @Body body: Map<String, String>
    ): Response<FormularioAdopcionDto>

    @DELETE("formularios/{id}")
    suspend fun eliminarFormulario(
        @Path("id") id: Long,
        @Query("emailAdmin") emailAdmin: String
    ): Response<Unit>

    companion object {

        private fun createOkHttpClient(): OkHttpClient {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            return OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(ApiConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(ApiConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(ApiConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build()
        }

        private fun createRetrofit(baseUrl: String): Retrofit {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(createOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        // Clientes para cada microservicio
        fun createAuthService(): ApiService =
            createRetrofit(ApiConfig.getAuthBaseUrl()).create(ApiService::class.java)

        fun createCatalogoService(): ApiService =
            createRetrofit(ApiConfig.getCatalogoBaseUrl()).create(ApiService::class.java)

        fun createAnimalesService(): ApiService =
            createRetrofit(ApiConfig.getAnimalesBaseUrl()).create(ApiService::class.java)

        fun createCarritoService(): ApiService =
            createRetrofit(ApiConfig.getCarritoBaseUrl()).create(ApiService::class.java)

        fun createFormularioService(): ApiService =
            createRetrofit(ApiConfig.getFormularioBaseUrl()).create(ApiService::class.java)
    }
}