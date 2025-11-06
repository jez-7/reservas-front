package com.turnos.model

import android.net.Uri

// --- 1. MODELO DE DATOS para el perfil del negocio (DTO) ---
data class ProfileDto(
    val urlSlug: String = "",
    val email: String = "",
    val address: String = "",
    val description: String = "",
    val profileImageUrl: String? = null
)