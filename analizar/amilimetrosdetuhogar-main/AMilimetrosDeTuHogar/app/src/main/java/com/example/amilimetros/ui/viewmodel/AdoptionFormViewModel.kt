package com.example.amilimetros.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amilimetros.data.local.storage.UserPreferences
import com.example.amilimetros.data.repository.FormularioApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AdoptionFormState {
    object Idle : AdoptionFormState()
    object Loading : AdoptionFormState()
    object Success : AdoptionFormState()
    data class Error(val message: String) : AdoptionFormState()
}

class AdoptionFormViewModel(
    private val formularioRepository: FormularioApiRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _formState = MutableStateFlow<AdoptionFormState>(AdoptionFormState.Idle)
    val formState: StateFlow<AdoptionFormState> = _formState

    fun submitAdoptionForm(
        animalId: Long,
        direccion: String,
        tipoVivienda: String,
        tieneMallasVentanas: Boolean,
        viveEnDepartamento: Boolean,
        tieneOtrosAnimales: Boolean,
        motivoAdopcion: String
    ) {
        viewModelScope.launch {
            _formState.value = AdoptionFormState.Loading

            try {
                val userId = userPreferences.getUserId()
                if (userId == null) {
                    _formState.value = AdoptionFormState.Error("Usuario no autenticado")
                    return@launch
                }

                val result = formularioRepository.crearFormulario(
                    usuarioId = userId,
                    animalId = animalId,
                    direccion = direccion,
                    tipoVivienda = tipoVivienda,
                    tieneMallasVentanas = tieneMallasVentanas,
                    viveEnDepartamento = viveEnDepartamento,
                    tieneOtrosAnimales = tieneOtrosAnimales,
                    motivoAdopcion = motivoAdopcion
                )

                if (result.isSuccess) {
                    _formState.value = AdoptionFormState.Success
                } else {
                    _formState.value = AdoptionFormState.Error(
                        result.exceptionOrNull()?.message ?: "Error al enviar formulario"
                    )
                }
            } catch (e: Exception) {
                _formState.value = AdoptionFormState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun resetState() {
        _formState.value = AdoptionFormState.Idle
    }
}