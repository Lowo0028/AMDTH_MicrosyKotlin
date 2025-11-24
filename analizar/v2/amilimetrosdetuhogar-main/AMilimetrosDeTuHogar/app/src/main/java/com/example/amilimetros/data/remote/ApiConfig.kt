package com.example.amilimetros.data.remote

object ApiConfig {


    private const val BASE_IP = "192.168.1.10"

    private const val AUTH_BASE_URL = "https://x02n8cpn-8090.brs.devtunnels.ms/"
    private const val CATALOGO_BASE_URL = "https://x02n8cpn-8091.brs.devtunnels.ms/"
    private const val CARRITO_BASE_URL = "https://x02n8cpn-8092.brs.devtunnels.ms/"
    private const val ANIMALES_BASE_URL = "https://x02n8cpn-8093.brs.devtunnels.ms/"
    private const val FORMULARIO_BASE_URL = "https://x02n8cpn-8094.brs.devtunnels.ms/"

    private const val ORDEN_BASE_URL = "https://x02n8cpn-8095.brs.devtunnels.ms/"


    private const val LOGO_URL = "" // p.ej. "http://$BASE_IP:8091/assets/logo.png" o "" si no tienes

    const val TIMEOUT_SECONDS = 30L

    fun getAuthBaseUrl() = AUTH_BASE_URL
    fun getCatalogoBaseUrl() = CATALOGO_BASE_URL
    fun getAnimalesBaseUrl() = ANIMALES_BASE_URL
    fun getCarritoBaseUrl() = CARRITO_BASE_URL
    fun getFormularioBaseUrl() = FORMULARIO_BASE_URL

    fun getOrdenBaseUrl() = ORDEN_BASE_URL

    fun getLogoUrl(): String = LOGO_URL
}
