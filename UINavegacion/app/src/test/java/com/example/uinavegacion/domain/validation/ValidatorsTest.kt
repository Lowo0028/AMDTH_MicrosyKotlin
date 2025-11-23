package com.example.uinavegacion.domain.validation

import org.junit.Assert.assertNull
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ValidatorsTest {

    @Test
    fun validateEmail_ok(){
        val error = validateEmail("personal@gmail.com")
        assertNull(error)
    }

    @Test
    fun validateEmail_error_con_email_vacio(){
        val error = validateEmail("")
        assertEquals("El email es obligatorio", error)
    }

}