package com.turnos.di

import android.content.Context
import com.turnos.network.ApiService
import com.turnos.network.RetrofitClient
import com.turnos.network.TokenManager
import com.turnos.viewmodel.*
import androidx.lifecycle.ViewModelProvider

interface AppContainer {
    val homeViewModelFactory: ViewModelProvider.Factory
    val loginViewModelFactory: ViewModelProvider.Factory
}

class DefaultAppContainer(private val applicationContext: Context) {

    // ---  CAPA DE DATOS Y ALMACENAMIENTO ---

    // maneja el jwt
    private val tokenManager: TokenManager by lazy {
        TokenManager(applicationContext)
    }

    // instancia de ApiService (Retrofit)
    // se inicializa con el TokenManager para incluir el JWT en las peticiones
    private val apiService: ApiService by lazy {
        RetrofitClient.getApiService(tokenManager)
    }


    // --- FACTORIES DE VIEWMODEL ---

    val homeViewModelFactory: ViewModelProvider.Factory by lazy {
        HomeViewModelFactory(
            apiService = apiService,
            tokenManager = tokenManager
        )
    }

    val loginViewModelFactory: ViewModelProvider.Factory by lazy {
        LoginViewModelFactory(
            apiService = apiService,
            tokenManager = tokenManager
        )
    }

    val registerViewModelFactory: ViewModelProvider.Factory by lazy {
        RegisterViewModelFactory(
            apiService = apiService,
        )
    }

    val servicesViewModelFactory: ViewModelProvider.Factory by lazy {
        ServicesViewModelFactory(
            apiService = apiService,
            tokenManager = tokenManager,
        )
    }

    val profileViewModelFactory: ViewModelProvider.Factory by lazy {
        ProfileViewModelFactory(
            apiService = apiService,
            tokenManager = tokenManager,
        )
    }

    val statisticsViewModelFactory: ViewModelProvider.Factory by lazy {
        StatisticsViewModelFactory(
            apiService = apiService,
            tokenManager = tokenManager,
        )
    }
}