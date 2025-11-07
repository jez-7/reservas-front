package com.turnos.model

import android.net.Uri
import com.google.gson.annotations.SerializedName

// --- 1. MODELO DE DATOS para el perfil del negocio (DTO) ---
data class ProfileDto(
    @SerializedName("slug")
    val urlSlug: String? = null,
    @SerializedName("email")
    val email: String = "",
    @SerializedName("direccion")
    val address: String? = null,
    @SerializedName("descripcion")
    val description: String? = null,
    @SerializedName("profileImageUrl")
    val profileImageUrl: String? = null
)