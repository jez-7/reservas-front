package com.turnos.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turnos.network.ApiService
import com.turnos.network.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.text.toIntOrNull
import kotlin.text.toDoubleOrNull
import com.turnos.model.*


// --- ESTADO UNIFICADO DE LA UI ---
data class ServicesUiState(
    val selectedTab: Int = 0, // 0 = Servicios, 1 = Personal
    val services: List<ServiceDto> = emptyList(),
    val personal: List<PersonalDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ServicesViewModel(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServicesUiState())
    val uiState: StateFlow<ServicesUiState> = _uiState.asStateFlow()

    init {
        // Cargar ambas listas al inicio
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val token = tokenManager.getAuthToken()
            if (token.isEmpty()) return@launch // Manejar error de auth

            try {
                // Peticiones paralelas para ambas listas
                val servicesList = apiService.getAllServices(token = "Bearer $token")
                val personalList = apiService.getAllPersonal(token = "Bearer $token")

                _uiState.update {
                    it.copy(
                        services = servicesList,
                        personal = personalList,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar datos.") }
            }
        }
    }

    fun updateSelectedTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
    }

    // --- MANEJO DE DIALOGOS DE GUARDADO ---

    fun saveNewService(data: NewServiceRequest) {
        viewModelScope.launch {

            val durationString = data.duration.toString()
            val priceString = data.price.toString()

            val durationInt = durationString.toIntOrNull() ?: 0
            val priceDouble = priceString.toDoubleOrNull() ?: 0.0

            val token = tokenManager.getAuthToken()

            val request = NewServiceRequest(
                name = data.name,
                description = data.description,
                duration = durationInt,
                price = priceDouble
            )

            try {
                apiService.createService(
                    token = "Bearer $token",
                    request = request
                )
                _uiState.update { it.copy(error = null) }
                fetchData()
            } catch (e: Exception) {
                Log.e("ServicesVM", "Error al crear servicio", e)
                _uiState.update { it.copy(error = "Error al guardar servicio: ${e.message}") }
            }
        }
    }

    fun saveNewPersonal(data: NewPersonalRequest) {
        viewModelScope.launch {


            val token = tokenManager.getAuthToken()
            val request = NewPersonalRequest(
                name = data.name,
                email = data.email,
                phone = data.phone,
                password = data.password
            )
            apiService.createPersonal(

                token = "Bearer $token",
                request = request
            )
            fetchData() // recargar listas
        }
    }

    fun deleteService(serviceId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val token = tokenManager.getAuthToken()

            if (token.isEmpty()) {
                _uiState.update { it.copy(isLoading = false, error = "Autenticación requerida para eliminar.") }
                return@launch
            }

            try {
                // Llama al endpoint DELETE del ApiService
                apiService.delete(token = "Bearer $token", serviceId = serviceId)

                // Si es exitoso, recarga la lista para actualizar la UI
                fetchData()

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al eliminar servicio: ${e.message}") }
            }
        }
    }

    // --- FUNCIÓN PARA ELIMINAR PERSONAL ---
    fun deletePersonal(personalId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val token = tokenManager.getAuthToken()

            if (token.isEmpty()) {
                _uiState.update { it.copy(isLoading = false, error = "Autenticación requerida para eliminar.") }
                return@launch
            }

            try {
                // Llama al endpoint DELETE del ApiService
                apiService.deletePersonal(token = "Bearer $token", personalId = personalId)

                // Si es exitoso, recarga la lista para actualizar la UI
                fetchData()

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al eliminar personal: ${e.message}") }
            }
        }
    }
}