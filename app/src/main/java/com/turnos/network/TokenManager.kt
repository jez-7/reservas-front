package com.turnos.network

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Define el nombre del archivo DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

// Define la llave única para guardar el token
val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")

class TokenManager(private val context: Context) {

    // --- 1. FUNCIÓN PARA GUARDAR EL TOKEN ---
    /**
     * Guarda el token JWT en el DataStore.
     */
    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = token
        }
    }

    // --- 2. FUNCIÓN PARA OBTENER EL TOKEN ---
    /**
     * Recupera el token JWT. Retorna una cadena vacía si no existe.
     */
    suspend fun getAuthToken(): String {
        return context.dataStore.data
            .map { preferences ->
                // devuelve el token
                preferences[AUTH_TOKEN_KEY] ?: ""
            }
            .first()
    }

    // --- 3. FUNCIÓN PARA ELIMINAR EL TOKEN (Cerrar Sesión) ---
    /**
     * Limpia el token al cerrar la sesión.
     */
    suspend fun clearAuthToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(AUTH_TOKEN_KEY)
        }
    }
}
