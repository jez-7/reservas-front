package com.turnos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turnos.model.LoginRequest
import com.turnos.network.ApiService
import com.turnos.network.TokenManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)

class LoginViewModel(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun authenticate(onSuccess: () -> Unit) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val request = LoginRequest(
                    email = _uiState.value.email,
                    password = _uiState.value.password
                )

                val response = apiService.login(request)

                // guardar el token JWT
                tokenManager.saveAuthToken(response.token)

                _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }

                onSuccess() // llama al callback de navegación

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error de conexión o credenciales incorrectas."
                    )
                }
            }
        }
    }
}