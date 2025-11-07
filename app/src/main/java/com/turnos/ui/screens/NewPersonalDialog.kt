package com.turnos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.turnos.model.NewPersonalRequest
import com.turnos.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPersonalDialog(
    onDismiss: () -> Unit,
    onSave: (NewPersonalRequest) -> Unit
) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") } // <-- Nuevo estado
    var passwordVisible by remember { mutableStateOf(false) }
    var phone by remember { mutableStateOf("") }

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
                    Text(text = "Nuevo Personal", color = DarkBackground, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Cerrar", tint = DarkBackground)
                    }
                }

                Divider(color = LightGrayText.copy(alpha = 0.5f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState())) {

                    // --- Campo Nombre Completo ---
                    OutlinedTextField(
                        value = name, onValueChange = { name = it },
                        label = { Text("Nombre Completo", color = DarkBackground.copy(alpha = 0.6f)) },
                        modifier = Modifier.fillMaxWidth(), singleLine = true, colors = colors, shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))



                    // --- Campo Correo Electrónico ---
                    OutlinedTextField(
                        value = email, onValueChange = { email = it },
                        label = { Text("Correo Electrónico", color = DarkBackground.copy(alpha = 0.6f)) },
                        modifier = Modifier.fillMaxWidth(), singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = colors, shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña", color = colors.unfocusedLabelColor) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector  = image, contentDescription = null, tint = LightGrayText)
                            }
                        },
                        colors = colors,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Campo Teléfono ---
                    OutlinedTextField(
                        value = phone, onValueChange = { phone = it.filter { char -> char.isDigit() } },
                        label = { Text("Teléfono", color = DarkBackground.copy(alpha = 0.6f)) },
                        modifier = Modifier.fillMaxWidth(), singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = colors, shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    // --- Botón Guardar ---
                    Button(
                        onClick = {
                            // Construir el DTO SÓLO con los campos requeridos
                            val data = NewPersonalRequest(name, email, phone, password)
                            onSave(data)
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CustomBlue),
                        shape = RoundedCornerShape(12.dp),
                        enabled = name.isNotBlank()  && email.isNotBlank() && password.isNotBlank()
                    ) {
                        Text("Guardar Personal", color = WhiteText, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ... Preview ...

@Preview(showBackground = true)
@Composable
fun NewPersonalDialogPreview() {
    TurnosTheme {
        Box(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
            NewPersonalDialog(
                onDismiss = { /* */ },
                onSave = { /* */ }
            )
        }
    }
}