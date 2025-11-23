package com.example.uinavegacion.navigation

// Clase sellada para rutas: evita "strings mágicos" y facilita refactors
sealed class Route(val path: String) { // Cada objeto representa una pantalla
    data object Home     : Route("home")     // Ruta Home
    data object Login    : Route("login")    // Ruta Login
    data object Register : Route("register") // Ruta Registro

    // Dentro de tu sealed class Route:
    data object PostsList : Route("posts")                       // Listado completo
    data object PostSearch : Route("posts/search")               // Buscar por ID
    data object PostAddEdit : Route("posts/addedit?postId={postId}") // Agregar/Editar con parámetro opcional
}

/*
* “Strings mágicos” se refiere a cuando pones un texto duro y repetido en varias partes del código,
* Si mañana cambias "home" por "inicio", tendrías que buscar todas las ocurrencias de "home" a mano.
* Eso es frágil y propenso a errores.
La idea es: mejor centralizar esos strings en una sola clase (Route), y usarlos desde ahí.*/