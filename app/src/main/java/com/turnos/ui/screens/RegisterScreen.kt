package com.turnos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


import com.turnos.ui.theme.CustomBlue
import com.turnos.ui.theme.DarkBackground
import com.turnos.ui.theme.DarkSurface
import com.turnos.ui.theme.LightGrayText
import com.turnos.ui.theme.TurnosTheme
import com.turnos.ui.theme.WhiteText


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegistrationSuccess: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    email: String,
    name: String,
    password: String,
    isLoading: Boolean,
    error: String?,
    isFormValid: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit // El evento que llama a viewModel.register()
) {

    var passwordVisible by remember { mutableStateOf(false) }


    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = DarkSurface,
        unfocusedContainerColor = DarkSurface,
        focusedBorderColor = CustomBlue,
        unfocusedBorderColor = DarkSurface,
        focusedTextColor = WhiteText,
        unfocusedTextColor = WhiteText,
        focusedLabelColor = LightGrayText,
        unfocusedLabelColor = LightGrayText
    )


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = WhiteText
                )
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Campo Nombre
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Nombre del negocio (sin espacios)", color = LightGrayText) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors,
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))


            // Campo Correo
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Correo Electrónico", color = LightGrayText) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Campo Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Contraseña", color = LightGrayText) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors,

                // visibilidad
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),

                // ocultar / mostrar contraseña
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Ocultar Contraseña" else "Mostrar Contraseña"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector  = image, contentDescription = description, tint = LightGrayText)
                    }
                },
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))

            // boton Registrarse
            Button(
                onClick = onRegisterClick,
                enabled = !isLoading && isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CustomBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = WhiteText, modifier = Modifier.size(24.dp))
                } else {
                    Text("Registrarme", color = WhiteText, fontWeight = FontWeight.Bold)
                }
            }

            if (error != null) {
                Text(
                    text = error,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ir al login
            TextButton(
                onClick = onNavigateToLogin, // Usamos el callback
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Ya tengo cuenta, Iniciar Sesión",
                    color = LightGrayText,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    TurnosTheme {
        RegisterScreen(
            onRegistrationSuccess = { println("Navegar a Home") },
            onNavigateToLogin = { println("Navegar a Login") },
            // --- ESTADOS SIMULADOS  ---
            email = "test@negocio.com",
            name = "Mi Barbershop",
            password = "password123",
            isLoading = false,
            error = null,
            isFormValid = true,

            // --- CALLBACKS DE EVENTOS DE ENTRADA  ---
            onNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},

            // --- EVENTO PRINCIPAL ---
            onRegisterClick = { println("Intento de Registro") }
        )
    }
}