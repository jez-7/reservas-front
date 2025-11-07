package com.turnos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turnos.navigation.Screen
import com.turnos.ui.theme.*
import com.turnos.viewmodel.ServicesUiState
import com.turnos.model.NewPersonalRequest
import com.turnos.model.NewServiceRequest
import com.turnos.model.ServiceDto
import com.turnos.model.PersonalDto

// --- 2. PANTALLA PRINCIPAL DE SERVICIOS ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    currentRoute: String,
    onNavigateTo: (route: String) -> Unit,

    // --- DATOS Y EVENTOS INYECTADOS DEL VIEWMODEL ---
    uiState: ServicesUiState, // <-- Recibe el estado completo (incluye lists y selectedTab)
    onUpdateTab: (Int) -> Unit, // <-- Callback para cambiar la pestaña
    onSaveService: (NewServiceRequest) -> Unit, // <-- Lógica para guardar servicio
    onSavePersonal: (NewPersonalRequest) -> Unit, // <-- Lógica para guardar personal
    onEditService: (ServiceDto) -> Unit,  // <-- Usar DTO
    onDeleteService: (ServiceDto) -> Unit, // <-- Usar DTO
    onEditPersonal: (PersonalDto) -> Unit, // <-- Nuevo callback necesario
    onDeletePersonal: (PersonalDto) -> Unit, // <-- Nuevo callback necesario
) {
    // --- ESTADOS LOCALES (SOLO PARA LA UI: MOSTRAR/OCULTAR DIALOGS) ---
    var showNewServiceDialog by remember { mutableStateOf(false) }
    var showNewPersonalDialog by remember { mutableStateOf(false) }

    // Obtenemos los datos y estados del UI state inyectado
    val services = uiState.services
    val personal = uiState.personal
    val selectedTab = uiState.selectedTab // <-- Usamos el estado observado

    Scaffold(
        topBar = { AppToolbar(title = "Servicios") },
        bottomBar = { BottomNavBar(
            currentRoute = currentRoute,
            onNavigateTo = onNavigateTo
        ) },
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Selector de Servicios / Personal
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF2C2C2C))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ToggleButton(
                    text = "Servicios",
                    icon = Icons.Filled.DesignServices,
                    isSelected = selectedTab == 0,
                    onClick = { onUpdateTab(0) }, // <-- LLAMA AL CALLBACK
                    modifier = Modifier.weight(1f)
                )
                ToggleButton(
                    text = "Personal",
                    icon = Icons.Filled.People,
                    isSelected = selectedTab == 1,
                    onClick = { onUpdateTab(1) }, // <-- LLAMA AL CALLBACK
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Botón Principal de Añadir ---
            Button(
                onClick = {
                    // Abre el diálogo correspondiente
                    if (selectedTab == 0) {
                        showNewServiceDialog = true
                    } else {
                        showNewPersonalDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CustomBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                val buttonText = when (selectedTab) {
                    0 -> "Nuevo Servicio"
                    1 -> "Nuevo Personal"
                    else -> "Añadir"
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Add, contentDescription = "Añadir", tint = WhiteText)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(buttonText, color = WhiteText, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- DIÁLOGOS MODALES (Fuera del flujo de lista) ---
            if (showNewServiceDialog) {
                NewServiceDialog(
                    onDismiss = { showNewServiceDialog = false },
                    onSave = { newServiceData ->
                        onSaveService(newServiceData) // <-- LLAMA AL CALLBACK
                        showNewServiceDialog = false
                    }
                )
            }

            if (showNewPersonalDialog) {
                NewPersonalDialog(
                    onDismiss = { showNewPersonalDialog = false },
                    onSave = { newPersonalData ->
                        onSavePersonal(newPersonalData) // <-- LLAMA AL CALLBACK
                        showNewPersonalDialog = false
                    }
                )
            }

            // --- CONTENIDO DE LA LISTA ---
            if (selectedTab == 0) {
                // Lista de Servicios
                if (services.isEmpty()) {
                    Text("No hay servicios disponibles.", color = LightGrayText, modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), textAlign = TextAlign.Center)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(services, key = { it.id }) { service ->
                            ServiceCard(
                                service = service,
                                onEdit = { onEditService(it) },
                                onDelete = { onDeleteService(it) } // <-- LLAMA AL CALLBACK
                            )
                        }
                    }
                }
            } else {
                // Lista de Personal
                if (personal.isEmpty()) {
                    Text("No hay personal registrado.", color = LightGrayText, modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), textAlign = TextAlign.Center)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(personal, key = { it.id }) { employee ->
                            PersonalCard(
                                person = employee,
                                onEdit = { onEditPersonal(it) }, // <-- LLAMA AL CALLBACK
                                onDelete = { onDeletePersonal(it) } // <-- LLAMA AL CALLBACK
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- 3. SUB-COMPONENTES ---

// Componente reutilizable para los botones de selección (Servicios/Personal)
@Composable
fun ToggleButton(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) CustomBlue else Color.Transparent,
            contentColor = if (isSelected) WhiteText else LightGrayText
        ),
        shape = RoundedCornerShape(10.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = text, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}

// Tarjeta individual para un Servicio
@Composable
fun ServiceCard(
    service: ServiceDto,
    onEdit: (ServiceDto) -> Unit,
    onDelete: (ServiceDto) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteText),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Imagen del servicio
            /* Image(
                 painter = painterResource(id = service.imageUrl),
                 contentDescription = service.name,
                 modifier = Modifier
                     .fillMaxWidth()
                     .height(180.dp)
                     .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                 contentScale = ContentScale.Crop // Asegura que la imagen llene el espacio
             )*/

            // Contenido de texto y botones
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = service.name ?: "",
                    color = DarkBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.AccessTime,
                        contentDescription = "Duración",
                        tint = LightGrayText,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${service.durationMinutes} minutos",
                        color = LightGrayText,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = service.description ?: "",
                    color = DarkBackground.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${String.format("%.2f", service.price)}", // Formato de moneda
                        color = DarkBackground,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp
                    )
                    Row {
                        IconButton(onClick = { onEdit(service) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = CustomBlue)
                        }
                        IconButton(onClick = { onDelete(service) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

// Componente: Tarjeta individual para un Empleado
@Composable
fun PersonalCard(
    person: PersonalDto,
    onEdit: (PersonalDto) -> Unit,
    onDelete: (PersonalDto) -> Unit
) {
    val statusColor = if (person.isActive) Color(0xFF4CAF50) else Color.Red // Verde para Activo, Rojo para Inactivo

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteText),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            /* Imagen del empleado
            Image(
                painter = painterResource(id = person.imageUrl),
                contentDescription = person.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop // Asegura que la imagen llene el espacio
            )*/

            // Contenido de texto y botones
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = person.name ?: "Personal sin nombre",
                    color = DarkBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Rol / Especialidad
                Text(
                    text = person.role ?: "",
                    color = DarkBackground.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Indicador de Estado (Activo / Inactivo)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (person.isActive) "Activo" else "Inactivo",
                            color = statusColor,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }

                    // Botones de Editar y Eliminar
                    Row {
                        IconButton(onClick = { onEdit(person) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Editar Personal", tint = CustomBlue)
                        }
                        IconButton(onClick = { onDelete(person) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Eliminar Personal", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ServicesScreenPreview() {

    // 1. Estados simulados
    // Usamos remember para que el Preview pueda modificar el estado
    var mockSelectedTab by remember { mutableStateOf(0) }

    val mockServices = emptyList<ServiceDto>()
    val mockPersonal = emptyList<PersonalDto>()

    // 2. Mock UiState que usa el estado local de la Preview
    val mockUiState = ServicesUiState(
        selectedTab = mockSelectedTab, // <-- Usa la variable local mutable
        services = mockServices,
        personal = mockPersonal,
        isLoading = false,
        error = null
    )

    TurnosTheme {
        ServicesScreen(
            currentRoute = Screen.Services.route,
            onNavigateTo = { },

            uiState = mockUiState,

            // 3. PASAR LA FUNCIÓN DE ACTUALIZACIÓN DEL ESTADO LOCAL
            onUpdateTab = { newTab ->
                mockSelectedTab = newTab // <--- ¡ESTO HACE QUE EL BOTÓN FUNCIONE EN EL PREVIEW!
            },

            // ... (resto de callbacks vacíos)
            onSaveService = { },
            onSavePersonal = { },
            onEditService = { },
            onDeleteService = { },
            onEditPersonal = { },
            onDeletePersonal = { }
        )
    }
}