package com.turnos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turnos.model.StatisticsResponseDto
import com.turnos.network.ApiService
import com.turnos.network.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth

// 1. estado de la Interfaz de Usuario (UiState)
data class StatisticsUiState(

    val statistics: StatisticsResponseDto? = null,
    val selectedMonth: YearMonth = YearMonth.now(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class StatisticsViewModel(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    // MutableStateFlow para modificar el estado
    private val _uiState = MutableStateFlow(StatisticsUiState(isLoading = true))
    // StateFlow publico para que la vista lo observe
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        fetchStatistics()
    }

    // --- CARGA DE DATOS ---
    fun fetchStatistics(month: YearMonth = _uiState.value.selectedMonth) {
        viewModelScope.launch {
            // empezar carga
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // obtener Token
            val token = tokenManager.getAuthToken()
            if (token.isEmpty()) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error: Token no disponible.") }
                return@launch
            }

            try {
                // Llamada a la API
                val stats = apiService.getStatistics(token = "Bearer $token")

                // Éxito: actualizar el estado con los datos
                _uiState.update { it.copy(statistics = stats, isLoading = false) }

            } catch (e: Exception) {
                // Error: actualizar el estado con el mensaje de error
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al cargar estadísticas: ${e.message}") }
            }
        }
    }

    fun navigateMonth(step: Int) {
        viewModelScope.launch {
            // 1. Calcula el nuevo mes
            val newMonth = _uiState.value.selectedMonth.plusMonths(step.toLong())

            // 2. Llama a la carga de datos para el nuevo mes (Asume que fetchStatistics acepta el mes)
            fetchStatistics(newMonth)

            // 3. Actualiza el estado de la UI (solo si la carga fue exitosa, pero la actualizamos para que la UI reaccione)
            _uiState.update { currentState ->
                currentState.copy(selectedMonth = newMonth)
            }
        }
    }
}