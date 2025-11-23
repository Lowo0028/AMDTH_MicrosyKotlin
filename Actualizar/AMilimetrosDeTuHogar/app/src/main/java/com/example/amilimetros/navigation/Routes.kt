sealed class Route(val path: String) {
    data object Home : Route("home")
    data object Login : Route("login")
    data object Register : Route("register")
    data object Products : Route("products")        // 🛒 Tienda
    data object Cart : Route("cart")                // 🛍️ Carrito real
    data object Animals : Route("animals")
    data object Admin : Route("admin")
    data object Profile : Route("profile")          // ✅ NUEVO
    data object OrderHistory : Route("order_history") // ✅ NUEVO
    data object AdoptionRequests : Route("adoption_requests") // ✅ NUEVO

    data object AdoptionForm : Route("adoption_form/{animalId}") {
        fun createRoute(animalId: Long) = "adoption_form/$animalId"
    }
    data object Location : Route("location")
}