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
    val errorMessage: String? = null
)

class HomeViewModel(
    // Inyección de dependencias (se asume que se pasan con un Factory/Hilt/Koin)
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // Carga inicial al iniciar la pantalla
        fetchAppointments()
    }

    // --- 2. FUNCIÓN PARA CARGAR TURNOS (GET) ---
    fun fetchAppointments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val token = tokenManager.getAuthToken()
            if (token.isEmpty()) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Autenticación requerida.") }
                return@launch
            }

            try {
                // Llama al endpoint de Retrofit para obtener todos los turnos
                val fetchedAppointments = apiService.getAllAppointments(token = "Bearer $token")

                // Actualiza el estado con los turnos recibidos
                _uiState.update {
                    it.copy(
                        appointments = fetchedAppointments,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar turnos: ${e.message}"
                    )
                }
            }
        }
    }

    // --- 3. FUNCIÓN PARA ELIMINAR TURNO (DELETE) ---
    fun deleteAppointment(appointmentId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val token = tokenManager.getAuthToken()

            try {
                // Llama al endpoint DELETE
                apiService.deleteAppointment(token = "Bearer $token", appointmentId = appointmentId)

                // Si la eliminación es exitosa en el backend, actualizamos el estado local
                _uiState.update { currentState ->
                    currentState.copy(
                        appointments = currentState.appointments.filter { it.id != appointmentId },
                        isLoading = false
                    )
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al eliminar: ${e.message}") }
            }
        }
    }

    // --- 4. GESTIÓN DE LA FECHA SELECCIONADA ---
    fun selectDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }

    }
}