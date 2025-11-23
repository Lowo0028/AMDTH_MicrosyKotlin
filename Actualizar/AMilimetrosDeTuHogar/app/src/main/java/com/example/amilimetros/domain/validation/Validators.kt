package com.example.amilimetros.domain.validation

import android.util.Patterns

// ========== VALIDACIÓN DE EMAIL ==========
// Valida que el email no esté vacío y cumpla patrón de email
fun validateEmail(email: String): String? {
    if (email.isBlank()) return "El email es obligatorio"
    val ok = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    return if (!ok) "Formato de email inválido" else null
}

// ========== VALIDACIÓN DE NOMBRE ==========
// Valida que el nombre contenga solo letras y espacios (sin números)
fun validateNameLettersOnly(name: String): String? {
    if (name.isBlank()) return "El nombre es obligatorio"
    val regex = Regex("^[A-Za-zÁÉÍÓÚÑáéíóúñ ]+$")
    return if (!regex.matches(name)) "Solo letras y espacios" else null
}

// ========== VALIDACIÓN DE TELÉFONO ==========
// Valida que el teléfono tenga solo dígitos y una longitud razonable
fun validatePhoneDigitsOnly(phone: String): String? {
    if (phone.isBlank()) return "El teléfono es obligatorio"
    if (!phone.all { it.isDigit() }) return "Solo números"
    if (phone.length !in 8..15) return "Debe tener entre 8 y 15 dígitos"
    return null
}

// ========== VALIDACIÓN DE CONTRASEÑA SEGURA ==========
// Valida seguridad de la contraseña (mín. 8, mayús, minús, número y símbolo; sin espacios)
fun validateStrongPassword(pass: String): String? {
    if (pass.isBlank()) return "La contraseña es obligatoria"
    if (pass.length < 8) return "Mínimo 8 caracteres"
    if (!pass.any { it.isUpperCase() }) return "Debe incluir una mayúscula"
    if (!pass.any { it.isLowerCase() }) return "Debe incluir una minúscula"
    if (!pass.any { it.isDigit() }) return "Debe incluir un número"
    if (!pass.any { !it.isLetterOrDigit() }) return "Debe incluir un símbolo"
    if (pass.contains(' ')) return "No debe contener espacios"
    return null
}

// ========== VALIDACIÓN DE CONFIRMACIÓN DE CONTRASEÑA ==========
// Valida que la confirmación coincida con la contraseña
fun validateConfirm(pass: String, confirm: String): String? {
    if (confirm.isBlank()) return "Confirma tu contraseña"
    return if (pass != confirm) "Las contraseñas no coinciden" else null
}

// ========== VALIDACIÓN DE PRECIO ==========
// Valida que el precio sea un número positivo
fun validatePrice(priceText: String): String? {
    if (priceText.isBlank()) return "El precio es obligatorio"
    val price = priceText.toDoubleOrNull()
    if (price == null) return "Ingresa un número válido"
    if (price <= 0) return "El precio debe ser mayor a 0"
    return null
}

// ========== VALIDACIÓN DE STOCK ==========
// Valida que el stock sea un número entero no negativo
fun validateStock(stockText: String): String? {
    if (stockText.isBlank()) return "El stock es obligatorio"
    val stock = stockText.toIntOrNull()
    if (stock == null) return "Ingresa un número entero"
    if (stock < 0) return "El stock no puede ser negativo"
    return null
}

// ========== VALIDACIÓN DE EDAD (para animales) ==========
// Valida que la edad sea un número positivo razonable
fun validateAge(ageText: String): String? {
    if (ageText.isBlank()) return "La edad es obligatoria"
    val age = ageText.toIntOrNull()
    if (age == null) return "Ingresa un número entero"
    if (age < 0) return "La edad no puede ser negativa"
    if (age > 30) return "Edad poco realista (máximo 30 años)"
    return null
}

// ========== VALIDACIÓN DE TEXTO GENERAL (no vacío) ==========
// Valida que un campo de texto no esté vacío
fun validateNotEmpty(text: String, fieldName: String = "Este campo"): String? {
    return if (text.isBlank()) "$fieldName es obligatorio" else null
}

// ========== VALIDACIÓN DE CATEGORÍA ==========
// Valida que se haya seleccionado una categoría
fun validateCategory(category: String): String? {
    if (category.isBlank()) return "Selecciona una categoría"
    return null
}

// ========== VALIDACIÓN DE ESPECIE (para animales) ==========
// Valida que se haya seleccionado una especie
fun validateSpecies(species: String): String? {
    if (species.isBlank()) return "Selecciona una especie"
    return null
}

// ========== VALIDACIÓN DE DESCRIPCIÓN ==========
// Valida que la descripción tenga un mínimo de caracteres
fun validateDescription(desc: String, minLength: Int = 10): String? {
    if (desc.isBlank()) return "La descripción es obligatoria"
    if (desc.length < minLength) return "Mínimo $minLength caracteres"
    return null
}

// ========== VALIDACIÓN DE CANTIDAD (carrito) ==========
// Valida que la cantidad sea mayor a 0
fun validateQuantity(qty: Int): String? {
    return if (qty <= 0) "La cantidad debe ser mayor a 0" else null
}

// ========== VALIDACIÓN DE URL (opcional) ==========
// Valida que una URL tenga formato correcto (si no está vacía)
fun validateImageUrl(url: String): String? {
    if (url.isBlank()) return null // URL opcional
    val urlPattern = Patterns.WEB_URL
    return if (!urlPattern.matcher(url).matches()) "URL inválida" else null
}