package com.turnos.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turnos.model.ProfileDto
import com.turnos.network.ApiService
import com.turnos.network.FileConverter
import com.turnos.network.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class ProfileUiState(
    val profile: ProfileDto? = null,
    val imageUrlUri: Uri? = null, // URI local de la imagen seleccionada
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val saveSuccess: Boolean = false
)

class ProfileViewModel(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        // carga el perfil actual al inicializar el ViewModel
        fetchProfile()
    }

    // --- FUNCIÓN PARA CARGAR PERFIL (GET) ---
    private fun fetchProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val token = tokenManager.getAuthToken()

            if (token.isEmpty()) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "No hay token de autenticación.") }
                return@launch
            }

            try {
                val profileDto = apiService.getProfile(token = "Bearer $token")
                _uiState.update { it.copy(profile = profileDto, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al cargar perfil: ${e.message}") }
            }
        }
    }


    // ---  FUNCIONES SETTER PARA ACTUALIZAR LOS CAMPOS DEL FORMULARIO ---


    /** actualiza el slug/url del negocio en el estado del perfil. */
    fun updateUrlSlug(slug: String) {
        _uiState.update { currentState ->
            val currentProfile = currentState.profile ?: ProfileDto()
            currentState.copy(profile = currentProfile.copy(urlSlug = slug))
        }
    }

    /** actualiza el email del negocio en el estado del perfil. */
    fun updateEmail(email: String) {
        _uiState.update { currentState ->
            val currentProfile = currentState.profile ?: ProfileDto()
            currentState.copy(profile = currentProfile.copy(email = email))
        }
    }

    /** actualiza la dirección del negocio en el estado del perfil. */
    fun updateAddress(address: String) {
        _uiState.update { currentState ->
            val currentProfile = currentState.profile ?: ProfileDto()
            currentState.copy(profile = currentProfile.copy(address = address))
        }
    }

    /** actualiza la descripción del negocio en el estado del perfil. */
    fun updateDescription(description: String) {
        _uiState.update { currentState ->
            val currentProfile = currentState.profile ?: ProfileDto()
            currentState.copy(profile = currentProfile.copy(description = description))
        }
    }

    /** almacena la Uri de la imagen seleccionada localmente. */
    fun updateImageUri(uri: Uri?) {
        _uiState.update { it.copy(imageUrlUri = uri) }
    }

    // --- FUNCION PARA GUARDAR PERFIL (POST/PUT) ---
    fun saveProfileChanges(
        profileData: ProfileDto,
        imageUri: Uri?,
        context: Context
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, saveSuccess = false) }
            val token = tokenManager.getAuthToken()

            if (token.isEmpty()) return@launch

            try {
                var finalProfileData = profileData

                // PASO 1: SUBIDA DE IMAGEN (Si hay una Uri local nueva)
                if (imageUri != null) {
                    // convertir Uri a MultipartBody.Part
                    val imagePart = FileConverter.uriToMultipart(context, imageUri)

                    if (imagePart != null) {
                        // llamar a Retrofit con el archivo binario
                        val newImageUrl = apiService.uploadProfileImage(
                            token = "Bearer $token",
                            image = imagePart
                        )
                        // actualizar el DTO con la nueva URL pública
                        finalProfileData = profileData.copy(profileImageUrl = newImageUrl)
                    }
                }

                // GUARDAR DATOS DEL PERFIL
                apiService.updateProfile(token = "Bearer $token", profileDto = finalProfileData)

                // exito
                _uiState.update { it.copy(profile = finalProfileData, isLoading = false, saveSuccess = true) }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al guardar: ${e.message}") }
            }
        }
    }
}