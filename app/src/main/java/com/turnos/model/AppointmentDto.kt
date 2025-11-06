package com.turnos.model

import androidx.compose.ui.graphics.Color
import java.time.LocalDate

data class AppointmentDto(
    val id: Int,
    val date: LocalDate,
    val time: String,
    val description: String,
    val color: Color // el color de la línea y el icono de borrar
)