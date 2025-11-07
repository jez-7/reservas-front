package com.turnos.model

import androidx.compose.ui.graphics.Color
import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.OffsetDateTime

data class AppointmentDto(
    val id: Long, // Cambiado de Int a Long

    @SerializedName("fechaTurno")
    val date: LocalDate,

    @SerializedName("horaInicio")
    val horaInicio: OffsetDateTime, // Cambiar de String a OffsetDateTime

    @SerializedName("horaFinal")
    val horaFinal: OffsetDateTime,

    val estado: String?,
    val notas: String?,

    @SerializedName("negocioId")
    val negocioId: Long,

    val servicio: ServicioBasicDto?,
    val cliente: ClienteBasicDto?,
    val empleado: EmpleadoBasicDto?
) {
    // Propiedades calculadas para mantener compatibilidad con la UI existente
    val time: String
        get() = horaInicio.toLocalTime().toString().substring(0, 5) // "HH:mm"

    val description: String
        get() = "${servicio?.nombre ?: "Servicio"} - ${cliente?.nombre ?: "Cliente"}"

    val color: Color
        get() {
            // Convertir el color hex del servicio a Color de Compose
            val hexColor = servicio?.color?.removePrefix("#") ?: "667eea"
            return try {
                Color(android.graphics.Color.parseColor("#$hexColor"))
            } catch (e: Exception) {
                Color(0xFF667eea) // Color por defecto si falla
            }
        }
}