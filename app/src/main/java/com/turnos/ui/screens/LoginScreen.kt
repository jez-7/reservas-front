package com.turnos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.turnos.ui.theme.*
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    // CALLBACKS DE NAVEGACIÓN
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,

    // ESTADOS Y EVENTOS DEL VIEWMODEL
    email: String,
    password: String,
    isLoading: Boolean,
    error: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
) {

    var passwordVisible by remember { mutableStateOf(false) }

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
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = WhiteText
                )
            )
            Spacer(modifier = Modifier.height(64.dp))


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


            // campo email
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Correo Electrónico", color = LightGrayText) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = "Correo", tint = LightGrayText) },
                colors = textFieldColors,
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // campo password
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Contraseña", color = LightGrayText) },
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
                colors = textFieldColors,
                shape = RoundedCornerShape(8.dp),
                // mostrar error si existe
                isError = error != null
            )

            // mensaje de error
            if (error != null) {
                Text(
                    text = error,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Start).padding(start = 8.dp, top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))

            // --- BOTON INICIAR SESION ---
            Button(
                // llama al callback de navegación
                onClick = onLoginSuccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CustomBlue),
                shape = RoundedCornerShape(8.dp),
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = WhiteText, modifier = Modifier.size(24.dp))
                } else {
                    Text("Ingresar", color = WhiteText, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // button registro
            TextButton(
                onClick = onNavigateToRegister,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Soy nuevo, crear cuenta",
                    color = LightGrayText,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    TurnosTheme {
        LoginScreen(
            onLoginSuccess = { },
            onNavigateToRegister = { },
            // simulacion de estados para el Preview
            email = "test@gmail.com",
            password = "password123",
            isLoading = false,
            error = "Credenciales incorrectas.",
            onEmailChange = {},
            onPasswordChange = {}
        )
    }
}