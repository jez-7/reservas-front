package com.turnos.ui.screens


import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.rememberAsyncImagePainter
import com.turnos.model.ProfileDto
import com.turnos.navigation.Screen
import com.turnos.ui.theme.*
import com.turnos.viewmodel.*
import androidx.lifecycle.viewmodel.compose.viewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    currentRoute: String,
    onNavigateTo: (route: String) -> Unit,
    onSaveProfile: (ProfileDto) -> Unit, // Callback de navegación

    // --- DATOS Y EVENTOS INYECTADOS DEL VIEWMODEL ---
    uiState: ProfileUiState,
    onUrlSlugChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onImageSelected: (Uri?) -> Unit,
    onSaveClick: (ProfileDto, Uri?, Context) -> Unit // Función que guarda y sube la imagen
) {
    val context = LocalContext.current

    // Obtenemos los datos del DTO o usamos valores seguros por defecto
    val profileData = uiState.profile
    val currentDescriptionLength = profileData?.description?.length ?: 0

    // Colores de TextField (se mantienen)
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = DarkSurface,
        unfocusedContainerColor = DarkSurface,
        focusedBorderColor = CustomBlue,
        unfocusedBorderColor = DarkSurface,
        focusedTextColor = WhiteText,
        unfocusedTextColor = WhiteText,
        focusedLabelColor = LightGrayText,
        unfocusedLabelColor = LightGrayText,
        cursorColor = WhiteText,
        errorCursorColor = Color.Red,
        errorContainerColor = DarkSurface,
        errorBorderColor = Color.Red,
        errorLabelColor = Color.Red
    )

    // Launcher para abrir la galería
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            onImageSelected(uri) // <-- Llama al callback inyectado para actualizar el VM
        }
    )

    Scaffold(
        topBar = { AppToolbar(title = "Perfil") },
        bottomBar = { BottomNavBar(currentRoute = currentRoute, onNavigateTo = onNavigateTo) },
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(androidx.compose.foundation.rememberScrollState())
                    .padding(top = 16.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- FOTO DEL NEGOCIO ---
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(CustomBlue.copy(alpha = 0.5f))
                            .border(2.dp, CustomBlue, CircleShape)
                            .clickable { imagePickerLauncher.launch("image/*") }, // Ejecutar launcher
                        contentAlignment = Alignment.Center
                    ) {

                        val imageModel = uiState.imageUrlUri ?: profileData?.profileImageUrl

                        if (imageModel != null) {
                            // Muestra la imagen seleccionada (Uri) o la imagen remota (String URL)
                            Image(
                                painter = rememberAsyncImagePainter(model = imageModel),
                                contentDescription = "Foto del negocio seleccionada",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            // Muestra el placeholder
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = "Placeholder de perfil",
                                tint = WhiteText.copy(alpha = 0.7f),
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Foto del negocio", color = WhiteText, fontWeight = FontWeight.Bold, fontSize = 18.sp)

                }

                Spacer(modifier = Modifier.height(32.dp))
                Text(text = "Información del negocio", color = WhiteText, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp))

                // --- URL ---
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Web, contentDescription = "URL", tint = LightGrayText, modifier = Modifier.size(24.dp).padding(end = 8.dp))
                    OutlinedTextField(
                        value = profileData?.urlSlug ?: "",
                        onValueChange = { newValue ->
                            onUrlSlugChange(newValue)
                        },
                        label = { Text("URL del negocio (Sin espacios)", color = LightGrayText) },
                        modifier = Modifier.weight(1f), singleLine = true, colors = textFieldColors, shape = RoundedCornerShape(8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // --- CORREO ---
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Email, contentDescription = "Correo", tint = LightGrayText, modifier = Modifier.size(24.dp).padding(end = 8.dp))
                    OutlinedTextField(
                        value = profileData?.email ?: "",
                        onValueChange = onEmailChange, // <-- Usa el callback inyectado
                        label = { Text("Dirección de correo", color = LightGrayText) },
                        modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), colors = textFieldColors, shape = RoundedCornerShape(8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // --- DIRECCIÓN ---
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, contentDescription = "Ubicación", tint = LightGrayText, modifier = Modifier.size(24.dp).padding(end = 8.dp))
                    OutlinedTextField(
                        value = profileData?.address ?: "",
                        onValueChange = onAddressChange, // <-- Usa el callback inyectado
                        label = { Text("Dirección del local", color = LightGrayText) },
                        modifier = Modifier.weight(1f), singleLine = true, colors = textFieldColors, shape = RoundedCornerShape(8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // --- DESCRIPCIÓN ---
                Text(text = "Descripción", color = LightGrayText, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) // Etiqueta
                OutlinedTextField(
                    value = profileData?.description ?: "",
                    onValueChange = { if (it.length <= 500) onDescriptionChange(it) }, // <-- Usa el callback
                    label = { Text("Escribe una descripción...", color = LightGrayText) },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                    singleLine = false, colors = textFieldColors, shape = RoundedCornerShape(8.dp)
                )
                Text(text = "$currentDescriptionLength/500 caracteres", color = LightGrayText, fontSize = 12.sp, modifier = Modifier.fillMaxWidth().align(Alignment.End))
                Spacer(modifier = Modifier.height(16.dp))

            }

            // --- BOTÓN GUARDAR CAMBIOS ---
            Button(
                onClick = {
                    // 1. Crear el DTO con los datos actuales del formulario (del uiState)
                    val profileDto = uiState.profile ?: ProfileDto()

                    // 2. Llama al callback principal de guardado del ViewModel
                    onSaveClick(
                        profileDto, // DTO que contiene los últimos cambios
                        uiState.imageUrlUri,
                        context // El Context para la conversión de archivo
                    )

                    // 3. Callback de navegación/feedback
                    onSaveProfile(profileDto)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CustomBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Save, contentDescription = "Guardar", tint = WhiteText)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar Cambios", color = WhiteText, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- PREVIEW ---
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {

    // Simulación del DTO que vendría del servidor
    val mockProfileData = ProfileDto(
        urlSlug = "mi-negocio",
        email = "demo@turnos.com",
        address = "Calle Falsa 123",
        description = "Descripción de prueba.",
        profileImageUrl = null
    )

    // Simulación del UI State que el ViewModel mantendría
    val mockUiState = ProfileUiState(
        profile = mockProfileData,
        isLoading = false,
        errorMessage = null,
        // Agrega imageUrlUri si lo usaste en ProfileUiState
        imageUrlUri = null
    )

    TurnosTheme {
        ProfileScreen(
            currentRoute = Screen.Profile.route, // Usar la ruta correcta
            onNavigateTo = { },
            onSaveProfile = { println("Navegar después de guardar") },

            // --- INYECCIÓN DE DATOS Y EVENTOS SIMULADOS ---
            uiState = mockUiState,
            onUrlSlugChange = { },
            onEmailChange = { },
            onAddressChange = { },
            onDescriptionChange = { },
            onImageSelected = { },

            // Simulación de la función de guardado
            onSaveClick = { dto, uri, context ->
                println("Datos guardados: $dto, URI: $uri")
            }
        )
    }
}