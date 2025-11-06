package com.turnos.ui.screens



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.turnos.ui.theme.CustomBlue
import com.turnos.ui.theme.DarkBackground
import com.turnos.ui.theme.LightGrayText
import com.turnos.ui.theme.TurnosTheme
import com.turnos.ui.theme.WhiteText

@Composable
fun WelcomeScreen(
    // Funciones de navegación (callbacks) para que el componente sea reutilizable
    onNavigateToRegister: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackground // Fondo oscuro consistente
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Centrar todo el contenido verticalmente
        ) {
            // Título principal
            Text(
                text = "¡Bienvenido!",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = WhiteText
                ),
                modifier = Modifier.padding(bottom = 80.dp)
            )

            // --- Botón REGISTRARSE (Botón primario) ---
            Button(
                onClick = onNavigateToRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CustomBlue), // Azul primario
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Registrarse", color = WhiteText, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Botón INICIAR SESIÓN (Texto secundario) ---
            TextButton(
                onClick = onNavigateToLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Iniciar Sesión",
                    color = LightGrayText, // Color gris claro para texto secundario
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    TurnosTheme {
        // En el Preview, solo proporcionamos lambdas vacías
        WelcomeScreen(
            onNavigateToRegister = { println("Navegar a Registro") },
            onNavigateToLogin = { println("Navegar a Login") }
        )
    }
}