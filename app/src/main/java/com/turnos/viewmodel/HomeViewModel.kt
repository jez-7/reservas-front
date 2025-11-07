package com.turnos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turnos.model.AppointmentDto
import com.turnos.network.ApiService
import com.turnos.network.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

// --- 1. DEFINICIÓN DEL ESTADO DE LA UI ---
data class HomeUiState(
    val appointments: List<AppointmentDto> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

class HomeViewModel(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // Carga inicial
        fetchAppointments()
    }

    // --- FUNCIÓN PRINCIPAL PARA CARGAR TURNOS (GET) ---
    fun fetchAppointments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val token = tokenManager.getAuthToken()
            if (token.isEmpty()) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Autenticación requerida.") }
                return@launch
            }

            try {
                val fetchedAppointments = apiService.getAllAppointments(token = "Bearer $token")
                _uiState.update {
                    it.copy(
                        appointments = fetchedAppointments,
                        isLoading = false,
                        isRefreshing = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = "Error al cargar turnos: ${e.message}"
                    )
                }
            }
        }
    }
    fun refreshAppointments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            fetchAppointments()
        }
    }


    // --- 3. FUNCIÓN PARA ELIMINAR TURNO (DELETE) ---
    fun deleteAppointment(appointmentId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val token = tokenManager.getAuthToken()

            try {
                apiService.deleteAppointment("Bearer $token", appointmentId.toInt()) // 👈 el backend probablemente reciba Int
                _uiState.update { current ->
                    current.copy(
                        appointments = current.appointments.filter { it.id != appointmentId },
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Error al eliminar: ${e.message}")
                }
            }
        }
    }

    // --- 4. GESTIÓN DE LA FECHA SELECCIONADA ---
    fun selectDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }

    }
}