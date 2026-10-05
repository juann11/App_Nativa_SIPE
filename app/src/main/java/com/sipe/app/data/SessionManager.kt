package com.sipe.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sipe.app.domain.StreakCalculator
import com.sipe.app.domain.StreakInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "sipe_prefs")

data class StreakState(
    val current: Int = 0,
    val best: Int = 0,
    val points: Int = 0,
    val lastDate: String = "",
    val freezesLeft: Int = 1
)

class SessionManager(private val context: Context) {

    companion object {
        val LOGGED = booleanPreferencesKey("logged")
        val NAME = stringPreferencesKey("name")
        val EMAIL = stringPreferencesKey("email")
        val LAST_DATE = stringPreferencesKey("last_date")
        val STREAK = intPreferencesKey("streak")
        val BEST = intPreferencesKey("best")
        val POINTS = intPreferencesKey("points")
        val FREEZES = intPreferencesKey("freezes")
        val DARK = booleanPreferencesKey("dark")
    }

    val logged: Flow<Boolean> = context.dataStore.data.map { it[LOGGED] ?: false }
    val userName: Flow<String> = context.dataStore.data.map { it[NAME] ?: "Estudiante" }
    val userEmail: Flow<String> = context.dataStore.data.map { it[EMAIL] ?: "" }
    val dark: Flow<Boolean> = context.dataStore.data.map { it[DARK] ?: false }

    val streak: Flow<StreakState> = context.dataStore.data.map {
        StreakState(
            current = it[STREAK] ?: 0,
            best = it[BEST] ?: 0,
            points = it[POINTS] ?: 0,
            lastDate = it[LAST_DATE] ?: "",
            freezesLeft = it[FREEZES] ?: 1
        )
    }

    suspend fun login(name: String, email: String) {
        context.dataStore.edit {
            it[LOGGED] = true
            it[NAME] = name
            it[EMAIL] = email
        }
        registerDailyActivity()
    }

    suspend fun logout() {
        context.dataStore.edit { it[LOGGED] = false }
    }

    suspend fun setDark(value: Boolean) {
        context.dataStore.edit { it[DARK] = value }
    }

    /**
     * Sistema de rachas (lógica pura en StreakCalculator, aquí solo se persiste).
     * Entrar a la app o completar una tarea cuenta como actividad del día.
     */
    suspend fun registerDailyActivity(): StreakState {
        val prefs = context.dataStore.data.first()
        val result = StreakCalculator.registerActivity(
            StreakInput(
                current = prefs[STREAK] ?: 0,
                best = prefs[BEST] ?: 0,
                points = prefs[POINTS] ?: 0,
                lastDate = prefs[LAST_DATE]?.takeIf { it.isNotBlank() },
                freezesLeft = prefs[FREEZES] ?: 1
            )
        )

        val newState = StreakState(
            result.current, result.best, result.points, result.lastDate, result.freezesLeft
        )
        context.dataStore.edit {
            it[LAST_DATE] = newState.lastDate
            it[STREAK] = newState.current
            it[BEST] = newState.best
            it[POINTS] = newState.points
            it[FREEZES] = newState.freezesLeft
        }
        return newState
    }

    fun levelFor(points: Int): String = StreakCalculator.levelFor(points)
}
