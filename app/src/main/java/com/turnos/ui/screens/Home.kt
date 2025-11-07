package com.turnos.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turnos.ui.theme.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import com.turnos.navigation.Screen
import com.turnos.model.AppointmentDto
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
@Composable
fun isPreviewMode(): Boolean {
    return LocalInspectionMode.current
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    currentRoute: String,
    onNavigateTo: (route: String) -> Unit,
    appointments: List<AppointmentDto>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDeleteAppointment: (Long) -> Unit, // ✅ Cambiado de Int a Long
    onRefresh: () -> Unit, // ✅ NUEVO: Callback para refrescar
    isRefreshing: Boolean // ✅ NUEVO: Estado de carga
) {
    val topPaddingDp = if (isPreviewMode()) 24.dp else 80.dp

    val today = LocalDate.now()
    val currentMonthStart = today.withDayOfMonth(1)

    val startMonth = LocalDate.of(2025, 10, 1)
    val totalMonths = startMonth.until(currentMonthStart).months.toInt()
    val totalPageCount = 120

    val pagerState = rememberPagerState(
        initialPage = totalMonths,
        pageCount = { totalPageCount }
    )
    val scope = rememberCoroutineScope()

    // Cálculo dinámico de contadores
    val todayAppointmentsCount = remember(appointments) {
        appointments.count { it.date == LocalDate.now() }
    }
    val pendingAppointmentsCount = remember(appointments) {
        appointments.count { it.date.isAfter(LocalDate.now()) }
    }

    val currentDayAppointments = remember(appointments, selectedDate) {
        appointments.filter { it.date == selectedDate }
    }

    val visibleMonth = startMonth.plusMonths(pagerState.currentPage.toLong()).withDayOfMonth(1)

    Scaffold(
        topBar = { AppToolbar("Inicio") },
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onNavigateTo = onNavigateTo
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->

        // ✅ NUEVO: SwipeRefresh envuelve el contenido
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    CountCards(todayAppointmentsCount, pendingAppointmentsCount)
                    Spacer(modifier = Modifier.height(30.dp))
                }

                // Calendario interactivo
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = WhiteText),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            CalendarHeader(
                                month = visibleMonth,
                                onPreviousClick = {
                                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                                },
                                onNextClick = {
                                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.wrapContentHeight()
                            ) { page ->
                                val month = startMonth.plusMonths(page.toLong()).withDayOfMonth(1)

                                CalendarGrid(
                                    month = month,
                                    selectedDate = selectedDate,
                                    onDateSelected = onDateSelected,
                                    appointments = appointments
                                )
                            }
                        }
                    }
                }

                // Lista de turnos
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    TurnosListHeader(selectedDate)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (currentDayAppointments.isEmpty()) {
                    item {
                        Text(
                            "No hay turnos para ${selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM"))}.",
                            color = LightGrayText,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                } else {
                    items(currentDayAppointments, key = { it.id }) { appointment ->
                        AppointmentListItem(
                            appointment = appointment,
                            onDelete = onDeleteAppointment
                        )
                    }
                }
            }
        }
    }
}

// ✅ ACTUALIZADO: Cambiar el tipo de parámetro a Long
@Composable
fun AppointmentListItem(appointment: AppointmentDto, onDelete: (Long) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f)
                .background(DarkBackground),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(appointment.color)
                .clip(RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp)))

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "${appointment.time}: ",
                color = WhiteText,
                fontSize = 15.sp
            )
            Text(
                text = appointment.description,
                color = appointment.color,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        IconButton(onClick = { onDelete(appointment.id) }) {
            Icon(
                Icons.Filled.Delete,
                contentDescription = "Eliminar turno",
                tint = appointment.color.copy(alpha = 0.7f)
            )
        }
    }
}

// --- 3. SUB-COMPONENTES ---

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AppToolbar(
    title: String
) {
    TopAppBar(
        title = {
            Text(
                title,
                color = WhiteText,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        },
        navigationIcon = {
            IconButton(onClick = { /* Open drawer */ }) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = WhiteText)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
    )
}

@Composable
fun CountCards(todayCount: Int, pendingCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CountCard(title = "Turnos de hoy", count = todayCount, icon = Icons.Filled.CalendarToday, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(16.dp))
        CountCard(title = "Turnos pendientes", count = pendingCount, icon = Icons.Filled.AccessTime, modifier = Modifier.weight(1f))
    }
}

@Composable
fun CountCard(title: String, count: Int, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(90.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteText),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, color = LightGrayText, fontSize = 14.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = count.toString(),
                    color = DarkBackground,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(icon, contentDescription = null, tint = DarkBackground.copy(alpha = 0.6f), modifier = Modifier.padding(start = 4.dp).size(18.dp))
            }
        }
    }
}

@Composable
fun CalendarHeader(month: LocalDate, onPreviousClick: () -> Unit, onNextClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPreviousClick) {
            Icon(Icons.Filled.ChevronLeft, contentDescription = "Mes Anterior", tint = DarkBackground)
        }
        Text(
            text = month.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("es"))).replaceFirstChar { it.uppercase() },
            color = DarkBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        IconButton(onClick = onNextClick) {
            Icon(Icons.Filled.ChevronRight, contentDescription = "Mes Siguiente", tint = DarkBackground)
        }
    }
}

@Composable
fun CalendarGrid(
    month: LocalDate,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    appointments: List<AppointmentDto>
) {
    val firstDayOfMonth = month.withDayOfMonth(1)
    val daysInMonth = month.lengthOfMonth()
    // Ajustar para que Lunes sea 0 y Domingo sea 6
    val dayOfWeekOffset = (firstDayOfMonth.dayOfWeek.value - 1) % 7

    val daysOfWeek = listOf("LU", "MA", "MI", "JU", "VI", "SÁ", "DO")

    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Calculamos el ancho ideal para cada día (1/7 del ancho máximo disponible)
        val dayWidth = maxWidth / 7

        // Días de la semana - Header
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { dayName ->
                Box(
                    modifier = Modifier.width(dayWidth),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        dayName,
                        fontWeight = FontWeight.SemiBold,
                        color = LightGrayText
                    )
                }
            }
        }

        // Grid de días
        Column {
            Spacer(modifier = Modifier.height(19.dp))
            for (week in 0 until 6) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (dayOfWeek in 0..6) {
                        val dayIndex = week * 7 + dayOfWeek
                        val dayOfMonth = dayIndex - dayOfWeekOffset + 1

                        Box(
                            modifier = Modifier.width(dayWidth).height(40.dp), // <--- ASIGNAR ANCHO Y ALTURA DE CELDA
                            contentAlignment = Alignment.Center
                        ) {
                            if (dayOfMonth in 1..daysInMonth) {
                                val date = month.withDayOfMonth(dayOfMonth)
                                val isSelected = date == selectedDate
                                val hasAppointment = appointments.any { it.date == date }

                                DayItem(
                                    day = dayOfMonth,
                                    date = date,
                                    isCurrentMonth = true,
                                    isSelected = isSelected,
                                    hasAppointment = hasAppointment,
                                    onClick = { onDateSelected(date) },
                                    appointments = appointments

                                )
                            }
                            // Días del mes anterior/siguiente (opcional, para rellenar)
                            else if (dayOfMonth <= 0) {
                                val date = firstDayOfMonth.minusDays(dayOfMonth.toLong() * -1)
                                DayItem(day = date.dayOfMonth, date = date, isCurrentMonth = false, isSelected = false, hasAppointment = false, onClick = {}, appointments = appointments)
                            }
                            else if (dayOfMonth > daysInMonth) {
                                val date = firstDayOfMonth.plusMonths(1).withDayOfMonth(dayOfMonth - daysInMonth)
                                DayItem(day = date.dayOfMonth, date = date, isCurrentMonth = false, isSelected = false, hasAppointment = false, onClick = {}, appointments = appointments)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayItem(
    day: Int,
    date: LocalDate,
    isCurrentMonth: Boolean,
    isSelected: Boolean,
    hasAppointment: Boolean,
    onClick: () -> Unit,
    appointments: List<AppointmentDto>

) {
    // 1. Obtener el color del primer turno para este día (si existe)
    val turnColor = appointments.find { it.date == date }?.color ?: Color.Transparent

    // 2. color de FONDO del círculo
    val circleBackgroundColor = when {
        isSelected -> CustomBlue // Seleccionado siempre es azul fuerte
        hasAppointment -> turnColor.copy(alpha = 0.5f) // Si hay turno, usar color del turno (claro)
        else -> Color.Transparent // Si no, transparente
    }

    // 3. color del TEXTO
    val textColor = when {
        isSelected -> WhiteText
        !isCurrentMonth -> LightGrayText.copy(alpha = 0.5f) // Días fuera de mes más tenues
        else -> DarkBackground // Texto normal (Negro/Gris Oscuro)
    }

    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(circleBackgroundColor) // <-- Usar el color dinámico aquí
            .clickable(enabled = isCurrentMonth, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            color = textColor, // <-- Usar el color de texto dinámico aquí
            fontWeight = FontWeight.Medium
        )
    }
}


@Composable
fun TurnosListHeader(selectedDate: LocalDate) {
    val formatter = DateTimeFormatter.ofPattern("EEEE d", Locale.forLanguageTag("es"))
    val headerText = selectedDate.format(formatter).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.forLanguageTag("es")) else it.toString() }

    Text(
        text = headerText,
        color = WhiteText,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.Start)
    )
}



@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigateTo: (route: String) -> Unit
) {
    val selectedColor = CustomBlue
    val unselectedColor = LightGrayText
    val iconSize = 60.dp

    NavigationBar(

        containerColor = DarkBackground,
        modifier = Modifier.height(60.dp),
        tonalElevation = 0.dp
    ) {

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.Home,
                    contentDescription = "Inicio",
                    modifier = Modifier.size(iconSize) //
                )
            },
            selected = currentRoute == Screen.Home.route,
            onClick = { onNavigateTo(Screen.Home.route) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedColor,
                unselectedIconColor = unselectedColor,
                indicatorColor = DarkBackground
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Servicios",
                    modifier = Modifier.size(iconSize)
                )
            },
            selected = currentRoute == Screen.Services.route,
            onClick = { onNavigateTo(Screen.Services.route) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedColor,
                unselectedIconColor = unselectedColor,
                indicatorColor = DarkBackground
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.BarChart,
                    contentDescription = "Estadísticas",
                    modifier = Modifier.size(iconSize) //
                )
            },
            selected = currentRoute == Screen.Stats.route,
            onClick = { onNavigateTo(Screen.Stats.route) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedColor,
                unselectedIconColor = unselectedColor,
                indicatorColor = DarkBackground
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.Notifications,
                    contentDescription = "Notificaciones",
                    modifier = Modifier.size(iconSize)
                )
            },
            selected = false,
            onClick = { /* Navegar a Notificaciones */ },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedColor,
                unselectedIconColor = unselectedColor,
                indicatorColor = DarkBackground
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "Perfil",
                    modifier = Modifier.size(iconSize)
                )
            },
            selected = currentRoute == Screen.Profile.route,
            onClick = { onNavigateTo(Screen.Profile.route) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedColor,
                unselectedIconColor = unselectedColor,
                indicatorColor = DarkBackground
            )
        )


    }
}


