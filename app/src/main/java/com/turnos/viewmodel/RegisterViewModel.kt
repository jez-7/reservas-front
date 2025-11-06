package com.turnos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turnos.model.RegisterRequest
import com.turnos.network.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- 1. DEFINICIÓN DEL ESTADO DE LA UI ---
data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegistrationSuccessful: Boolean = false
)

class RegisterViewModel(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    // bandera para saber si el formulario es valido
    val isFormValid: StateFlow<Boolean> = _uiState.map { state ->
        state.name.isNotBlank() && state.email.isNotBlank() && state.password.length >= 6
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )


    // --- FUNCIONES PARA ACTUALIZAR EL ESTADO DEL FORMULARIO ---
    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    // --- FUNCION DE REGISTRO ---
    fun register(onSuccess: () -> Unit) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val request = RegisterRequest(
                    name = _uiState.value.name,
                    email = _uiState.value.email,
                    password = _uiState.value.password
                )

                // Llama al endpoint de Retrofit para enviar los datos
                apiService.register(request)

                // Si no lanza excepción, la operación fue exitosa (código 200/201)
                _uiState.update { it.copy(isLoading = false, isRegistrationSuccessful = true) }

                onSuccess() // Llama al callback para navegar a Login

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al registrar: ${e.message}"
                    )
                }
            }
        }
    }
}