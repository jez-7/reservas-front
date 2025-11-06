package com.turnos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.turnos.network.ApiService
import com.turnos.network.TokenManager

class StatisticsViewModelFactory(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            return StatisticsViewModel(apiService, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}