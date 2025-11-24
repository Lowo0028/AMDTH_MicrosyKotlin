package com.example.amilimetros.navigation

sealed class Route(val path: String) {
    data object Home : Route("home")
    data object Login : Route("login")
    data object Register : Route("register")
    data object Products : Route("products")
    data object Cart : Route("cart")
    data object Animals : Route("animals")
    data object Admin : Route("admin")
    data object Profile : Route("profile")
    data object OrderHistory : Route("order_history")
    data object AdoptionRequests : Route("adoption_requests")
    data object Location : Route("location")

    data object AdoptionForm : Route("adoption_form/{animalId}") {
        fun createRoute(animalId: Long) = "adoption_form/$animalId"
    }
}