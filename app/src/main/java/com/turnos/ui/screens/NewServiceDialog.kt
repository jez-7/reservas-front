package com.turnos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.turnos.model.NewServiceRequest
import com.turnos.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewServiceDialog(
    onDismiss: () -> Unit,
    onSave: (NewServiceRequest) -> Unit
) {
    // Estados del formulario
    var serviceName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    // Colores del TextField para fondo claro (ya definidos)
    val colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        focusedBorderColor = CustomBlue,
        unfocusedBorderColor = LightGrayText,
        focusedTextColor = DarkBackground,
        unfocusedTextColor = DarkBackground,
        focusedLabelColor = CustomBlue,
        unfocusedLabelColor = DarkBackground.copy(alpha = 0.6f)
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = WhiteText)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // --- HEADER DEL DIÁLOGO ---
                // ... (Row con Título y Botón X) ...
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Nuevo Servicio", color = DarkBackground, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Cerrar", tint = DarkBackground)
                    }
                }

                Divider(color = LightGrayText.copy(alpha = 0.5f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState())) {

                    // --- Campo Nombre del Servicio ---
                    OutlinedTextField(
                        value = serviceName, onValueChange = { serviceName = it },
                        label = { Text("Nombre del Servicio", color = DarkBackground.copy(alpha = 0.6f)) },
                        modifier = Modifier.fillMaxWidth(), singleLine = true, colors = colors, shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Campo Descripción (Altura Mínima) ---
                    OutlinedTextField(
                        value = description, onValueChange = { description = it },
                        label = { Text("Descripción", color = DarkBackground.copy(alpha = 0.6f)) },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp), // Altura para descripción
                        singleLine = false, colors = colors, shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Campo Duración (Numérico, Single Line) ---
                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it.filter { char -> char.isDigit() } },
                        label = { Text("Duración (minutos)", color = DarkBackground.copy(alpha = 0.6f)) },
                        modifier = Modifier.fillMaxWidth(), singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = colors, shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Campo Precio (Decimal, Single Line) ---
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it.filter { char -> char.isDigit() || char == '.' } },
                        label = { Text("Precio", color = DarkBackground.copy(alpha = 0.6f)) },
                        modifier = Modifier.fillMaxWidth(), singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        leadingIcon = { Icon(Icons.Filled.AttachMoney, contentDescription = "Moneda", tint = DarkBackground.copy(alpha = 0.6f)) },
                        colors = colors, shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    // --- Botón Guardar ---
                    Button(
                        onClick = {

                            val durationInt = duration.toIntOrNull() ?: 0
                            val priceDouble = price.toDoubleOrNull() ?: 0.0

                            val data = NewServiceRequest(
                                name = serviceName,
                                description = description,
                                duration = durationInt,
                                price = priceDouble
                            )

                            onSave(data)
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CustomBlue),
                        shape = RoundedCornerShape(12.dp),
                        enabled = serviceName.isNotBlank() && duration.isNotBlank() && price.isNotBlank()
                    ) {
                        Text("Guardar Servicio", color = WhiteText, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewServiceDialogPreview() {
    TurnosTheme {
        // Envolvemos el Preview en un Box con fondo oscuro para simular el modal
        Box(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
            NewServiceDialog(
                onDismiss = { /* */ },
                onSave = { /* */ }
            )
        }
    }
}