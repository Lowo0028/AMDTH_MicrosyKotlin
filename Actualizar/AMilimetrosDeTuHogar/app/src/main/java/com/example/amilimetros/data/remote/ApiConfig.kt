package com.example.amilimetros.data.remote

object ApiConfig {


    private const val BASE_IP = "192.168.1.10" // <- cambia esto

    private const val AUTH_BASE_URL = "http://$BASE_IP:8090/"
    private const val CATALOGO_BASE_URL = "https://x02n8cpn-8091.brs.devtunnels.ms/"
    private const val CARRITO_BASE_URL = "http://$BASE_IP:8092/"
    private const val ANIMALES_BASE_URL = "http://$BASE_IP:8093/"
    private const val FORMULARIO_BASE_URL = "http://$BASE_IP:8094/"

    // Opcional: endpoint público del logo (si tu backend lo sirve)
    private const val LOGO_URL = "" // p.ej. "http://$BASE_IP:8091/assets/logo.png" o "" si no tienes

    const val TIMEOUT_SECONDS = 30L

    fun getAuthBaseUrl() = AUTH_BASE_URL
    fun getCatalogoBaseUrl() = CATALOGO_BASE_URL
    fun getAnimalesBaseUrl() = ANIMALES_BASE_URL
    fun getCarritoBaseUrl() = CARRITO_BASE_URL
    fun getFormularioBaseUrl() = FORMULARIO_BASE_URL

    fun getLogoUrl(): String = LOGO_URL
}
