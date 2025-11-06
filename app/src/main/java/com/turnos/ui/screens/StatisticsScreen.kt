package com.turnos.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.turnos.model.*
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turnos.navigation.Screen
import com.turnos.ui.theme.*
import com.turnos.viewmodel.StatisticsUiState
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    currentRoute: String,
    onNavigateTo: (route: String) -> Unit,

    // --- DATOS Y EVENTOS INYECTADOS DEL VIEWMODEL ---
    uiState: StatisticsUiState,
    onNavigateMonth: (step: Int) -> Unit // callback para cambiar de mes

) {

    val stats = uiState.statistics


    Scaffold(
        topBar = { AppToolbar(title = "Estadísticas") },
        bottomBar = { BottomNavBar(currentRoute = currentRoute, onNavigateTo = onNavigateTo) },
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // --- 3. BARRA DE NAVEGACIÓN DE MESES ---
            MonthNavigator(
                month = uiState.selectedMonth, // Usar la fecha del estado
                onPreviousClick = { onNavigateMonth(-1) },
                onNextClick = { onNavigateMonth(1) }
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isLoading) {
                // Mostrar spinner de carga
                Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = WhiteText)
                }
            } else if (uiState.errorMessage != null) {
                // Mostrar error
                Text(uiState.errorMessage ?: "Error desconocido", color = Color.Red, modifier = Modifier.padding(24.dp))
            } else if (stats != null) {
                // --- 4. TARJETAS DE CONTADORES ---
                StatsCards(
                    turns = stats.totalTurns,
                    revenue = stats.totalRevenue,
                    clients = stats.uniqueClients,
                    cancellations = stats.cancellations
                )
                Spacer(modifier = Modifier.height(24.dp))

                // --- 5. SERVICIOS MÁS POPULARES (Tabla) ---
                PopularServicesTable(stats.popularServices)
                Spacer(modifier = Modifier.height(24.dp))

                // --- 6. GRÁFICO DE EVALUACIÓN DE INGRESOS ---
                RevenueChartCard(stats.dailyRevenueData)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

}

// --- SUB-COMPONENTES DE LA PANTALLA ---

// Componente para la navegación de meses
@Composable
fun MonthNavigator(month: YearMonth, onPreviousClick: () -> Unit, onNextClick: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("es"))

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onPreviousClick) {
            Icon(Icons.Filled.ChevronLeft, contentDescription = "Mes Anterior", tint = WhiteText)
        }
        Text(
            text = month.format(formatter).replaceFirstChar { it.uppercase() },
            color = WhiteText,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        IconButton(onClick = onNextClick) {
            Icon(Icons.Filled.ChevronRight, contentDescription = "Mes Siguiente", tint = WhiteText)
        }
    }
}

// Tarjetas de contadores (Turnos totales, Ingresos totales, etc.)
@Composable
fun StatsCards(turns: Int, revenue: Double, clients: Int, cancellations: Int) {
    // Definimos el contenedor Grid para las 4 tarjetas
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                title = "Turnos totales",
                value = turns.toString(),
                icon = Icons.Filled.CalendarToday,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Ingresos totales",
                value = "$${String.format("%,.0f", revenue)}",
                icon = Icons.Filled.AttachMoney,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                title = "Clientes únicos",
                value = clients.toString(),
                icon = Icons.Filled.Person,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Cancelaciones",
                value = cancellations.toString(),
                icon = Icons.Filled.Close,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// Tarjeta individual de estadística
@Composable
fun StatCard(title: String, value: String, icon: ImageVector, modifier: Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteText),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, color = LightGrayText, fontSize = 14.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = value,
                    color = DarkBackground,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    icon,
                    contentDescription = null,
                    tint = DarkBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(start = 4.dp).size(18.dp)
                )
            }
        }
    }
}

// Tabla de Servicios Más Populares
@SuppressLint("DefaultLocale")
@Composable
fun PopularServicesTable(services: List<PopularServiceDto>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteText),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Servicios Más Populares",
                color = DarkBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            // --- HEADER DE LA TABLA ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text("Servicio", fontWeight = FontWeight.SemiBold, color = LightGrayText, modifier = Modifier.weight(3f))

                Text("Turnos", fontWeight = FontWeight.SemiBold, color = LightGrayText, modifier = Modifier.weight(1.5f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)

                Text("Ingresos", fontWeight = FontWeight.SemiBold, color = LightGrayText, modifier = Modifier.weight(2f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 1.dp,
                color = LightGrayText.copy(alpha = 0.5f)
            )

            // --- FILAS DE DATOS ---
            services.forEach { service ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Servicio (Nombre y Precio por turno)
                    Column(modifier = Modifier.weight(3f)) {
                        Text(service.serviceName, color = DarkBackground, fontSize = 14.sp)
                        Text("$${String.format("%.2f", service.pricePerTurn)} por turno", color = LightGrayText, fontSize = 12.sp)
                    }
                    // Turnos
                    Text(service.totalTurns.toString(), color = DarkBackground, modifier = Modifier.weight(1.5f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    // Ingresos
                    Text("$${String.format("%,.2f", service.totalRevenue)}", color = DarkBackground, modifier = Modifier.weight(2f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
                }
            }
        }
    }
}

// Tarjeta y Componente de Gráfico de Ingresos (Simulación)
@Composable
fun RevenueChartCard(data: List<DailyRevenueDto>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteText),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Evaluación de ingresos",
                color = DarkBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            )

            // --- Componente de Gráfico Simulado ---
            LineChartSimulated(data)
        }
    }
}

// Implementación Sencilla de un Gráfico de Líneas usando Canvas
@Composable
fun LineChartSimulated(data: List<DailyRevenueDto>) {
    if (data.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
            Text("No hay datos de ingresos para este mes.", color = LightGrayText)
        }
        return
    }

    val maxAmount = data.maxOfOrNull { it.amount } ?: 1f
    val minDay = data.minOfOrNull { it.day } ?: 1
    val maxDay = data.maxOfOrNull { it.day } ?: 30

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .padding(top = 16.dp, bottom = 8.dp)
    ) {
        val width = size.width
        val height = size.height
        val daysCount = maxDay - minDay + 1 // Días en el rango

        // Escala X (días) y Y (ingresos)
        val xStep = width / daysCount.toFloat()
        val yFactor = height / maxAmount

        val path = Path()

        data.forEachIndexed { index, revenue ->

            val x = (revenue.day - minDay) * xStep
            val y = height - (revenue.amount * yFactor)

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }

            // Dibuja el punto de dato
            drawCircle(
                color = CustomBlue,
                center = Offset(x, y),
                radius = 5f
            )
        }
        
        drawPath(
            path = path,
            color = CustomBlue,
            style = Stroke(width = 5f)
        )
    }
    // Etiquetas inferiores de los días (simulación de eje X)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Día $minDay", style = MaterialTheme.typography.labelSmall, color = LightGrayText)
        Text("Día $maxDay", style = MaterialTheme.typography.labelSmall, color = LightGrayText)
    }

}


@Preview(showBackground = true)
@Composable
fun StatisticsScreenPreview() {
    // ⚠️ Para que el Preview funcione, debes simular el estado del ViewModel
    val mockStats = StatisticsResponseDto(
        totalTurns = 15,
        totalRevenue = 8900.00,
        uniqueClients = 8,
        cancellations = 2,
        popularServices = listOf(
            PopularServiceDto("Corte", 3000.0, 5, 15000.0),
            PopularServiceDto("Tinte", 4000.0, 2, 8000.0)
        ),
        dailyRevenueData = listOf(
            DailyRevenueDto(5, 500f),
            DailyRevenueDto(10, 2000f),
            DailyRevenueDto(15, 8900f),
            DailyRevenueDto(25, 4500f)
        )
    )

    val mockUiState = StatisticsUiState(
        statistics = mockStats,
        isLoading = false,
        selectedMonth = YearMonth.now()
    )

    TurnosTheme {
        StatisticsScreen(
            currentRoute = Screen.Stats.route,
            onNavigateTo = {},
            uiState = mockUiState,
            onNavigateMonth = {}
        )
    }
}