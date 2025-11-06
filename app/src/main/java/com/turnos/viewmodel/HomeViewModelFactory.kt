package com.turnos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.turnos.network.ApiService
import com.turnos.network.TokenManager

class HomeViewModelFactory(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verifica que la clase solicitada sea HomeViewModel
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            // Devuelve una nueva instancia, pasándole las dependencias requeridas
            return HomeViewModel(apiService, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}