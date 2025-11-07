package com.turnos.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.turnos.navigation.Screen
import com.turnos.ui.screens.LoginScreen
import com.turnos.ui.screens.RegisterScreen
import com.turnos.ui.screens.WelcomeScreen
import com.turnos.ui.screens.HomeScreen
import com.turnos.ui.screens.ProfileScreen
import com.turnos.ui.screens.ServicesScreen
import com.turnos.ui.screens.StatisticsScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.turnos.AppTurnos
import com.turnos.viewmodel.*
@Composable
fun AppNavigation() {
    // 1. se crea el controlador de navegación
    val navController = rememberNavController()
    val context = LocalContext.current
    val appContainer = remember { (context.applicationContext as AppTurnos).appContainer }

    // 2. definir el NavHost
    NavHost(
        navController = navController,
        // aca define la primera pantalla que va a aparecer (WelcomeScreen)
        startDestination = Screen.Welcome.route
    ) {

        // --- WELCOME SCREEN ---
        composable(Screen.Welcome.route) {
            WelcomeScreen(

                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },

                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        // --- LOGIN SCREEN ---
        composable(Screen.Login.route) {
            // acceder a la Factory desde el AppContainer
            val context = LocalContext.current
            val appContainer = remember { (context.applicationContext as AppTurnos).appContainer }
            val loginFactory = remember { appContainer.loginViewModelFactory }

            // inyectar el ViewModel con la Factory
            val viewModel: LoginViewModel = viewModel(factory = loginFactory)
            val uiState by viewModel.uiState.collectAsState() // Observar el estado

            LoginScreen(
                onLoginSuccess = {
                    viewModel.authenticate {
                        // si la autenticación fue exitosa navega a home
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                // PASAR EL ESTADO Y EVENTOS AL COMPOSE
                email = uiState.email,
                password = uiState.password,
                onEmailChange = viewModel::updateEmail,
                onPasswordChange = viewModel::updatePassword,
                isLoading = uiState.isLoading,
                error = uiState.error
            )
        }

        // --- REGISTER SCREEN ---
        composable(Screen.Register.route) {

            // 1. instanciar la Factory para el ViewModel
            val registerFactory = remember { appContainer.registerViewModelFactory }

            // 2. inyectar el ViewModel
            val viewModel: RegisterViewModel = viewModel(factory = registerFactory)

            // 3. observar el estado
            val uiState by viewModel.uiState.collectAsState()
            val isFormValid by viewModel.isFormValid.collectAsState() // para habilitar el buton

            RegisterScreen(
                // 1. CALLBACK PRINCIPAL DE NAVEGACION
                onRegistrationSuccess = {
                    viewModel.register {
                        // navega a login solo si el registro en el backend fue exitoso
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                },

                // 2. NAVEGACION SECUNDARIA
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },

                // 3. INYECCION DE DATOS Y ESTADO DEL VIEWMODEL
                name = uiState.name,
                email = uiState.email,
                password = uiState.password,
                isLoading = uiState.isLoading,
                error = uiState.error,
                isFormValid = isFormValid,

                // 4. EVENTOS
                onNameChange = viewModel::updateName,
                onEmailChange = viewModel::updateEmail,
                onPasswordChange = viewModel::updatePassword,

                onRegisterClick = {
                    viewModel.register {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        // --- STATS SCREEN ---
        composable(Screen.Stats.route) {

            val statsFactory = remember { appContainer.statisticsViewModelFactory }
            val viewModel: StatisticsViewModel = viewModel(factory = statsFactory)

            val uiState by viewModel.uiState.collectAsState()


            StatisticsScreen(
                currentRoute = Screen.Stats.route,
                onNavigateTo = { route ->
                    navController.navigate(route) {
                        // Lógica de BottomNavBar
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                uiState = uiState,
                onNavigateMonth = viewModel::navigateMonth

            )
        }



        // --- HOME SCREEN ---
        composable(Screen.Home.route) {
            val homeFactory = remember { appContainer.homeViewModelFactory }

            // 3. Instanciar el ViewModel USANDO LA FACTORY
            val viewModel: HomeViewModel = viewModel(factory = homeFactory)

            // 4. Observar el estado de la UI
            val uiState by viewModel.uiState.collectAsState()

            HomeScreen(

                // INYECCIÓN DE DATOS Y LÓGICA DEL VIEWMODEL
                appointments = uiState.appointments,
                selectedDate = uiState.selectedDate,
                onDateSelected = viewModel::selectDate,
                onDeleteAppointment = viewModel::deleteAppointment,
                currentRoute = Screen.Home.route,
                onNavigateTo = { route ->
                    navController.navigate(route) {
                        // Lógica de BottomNavBar
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        // --- SERVICES SCREEN ---
        composable(Screen.Services.route) {

            // obtener el AppContainer y la Factory
            val context = LocalContext.current
            val appContainer = remember { (context.applicationContext as AppTurnos).appContainer }
            val servicesFactory = remember { appContainer.servicesViewModelFactory }

            // inyectar el ViewModel usando la Factory
            val viewModel: ServicesViewModel = viewModel(factory = servicesFactory)

            // observar el estado de la UI
            val uiState by viewModel.uiState.collectAsState()

            // pasar los estados y callbacks a la pantalla
            ServicesScreen(
                currentRoute = Screen.Services.route,
                onNavigateTo = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },


                onSaveService = viewModel::saveNewService,
                onSavePersonal = viewModel::saveNewPersonal,
                onEditService = { /* ... */ },
                onDeleteService = { serviceDto -> viewModel.deleteService(serviceDto.id) },


                uiState = uiState,
                onUpdateTab = viewModel::updateSelectedTab,
                onEditPersonal = { /* ... */ },
                onDeletePersonal = { personalDto -> viewModel.deletePersonal(personalDto.id) },
            )
        }

        // --- PROFILE SCREEN ---
        composable(Screen.Profile.route) {

            // 1. Obtener la Factory e inyectar el ViewModel
            val profileFactory = remember { appContainer.profileViewModelFactory }
            val viewModel: ProfileViewModel = viewModel(factory = profileFactory)

            // 2. Observar el estado
            val uiState by viewModel.uiState.collectAsState()

            ProfileScreen (
                // --- NAVEGACION ---
                currentRoute = Screen.Profile.route,
                onNavigateTo = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },

                onSaveProfile = {
                    navController.popBackStack()
                },

                // --- INYECCIÓN DE DATOS Y LÓGICA DEL VIEWMODEL ---

                // Datos de estado para la UI
                uiState = uiState,

                // Setters (Eventos de texto)
                onUrlSlugChange = viewModel::updateUrlSlug,
                onEmailChange = viewModel::updateEmail,
                onAddressChange = viewModel::updateAddress,
                onDescriptionChange = viewModel::updateDescription,

                // Evento de Imagen (Actualiza la URI local en el VM)
                onImageSelected = viewModel::updateImageUri,

                // Función principal de guardado (incluye subir archivo y guardar JSON)
                onSaveClick = viewModel::saveProfileChanges
            )
        }
    }
}