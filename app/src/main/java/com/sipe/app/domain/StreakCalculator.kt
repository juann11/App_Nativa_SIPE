package com.sipe.app.domain

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Lógica pura de rachas (sin Android): testeable en JVM.
 * Regla: un día cuenta como activo si el usuario abre la app o completa una tarea.
 * - Mismo día: no cambia la racha, +2 pts.
 * - Día consecutivo: racha +1, +10 pts (+50 bonus cada 7 días, +1 freeze hasta 3).
 * - 1 día perdido con freeze disponible: se consume el freeze y la racha continúa.
 * - Brecha mayor o sin freeze: reinicia a 1, +10 pts.
 * Fechas en ISO (AAAA-MM-DD), zona del dispositivo. Guardar solo la fecha.
 */
data class StreakInput(
    val current: Int = 0,
    val best: Int = 0,
    val points: Int = 0,
    val lastDate: String? = null,
    val freezesLeft: Int = 1
)

data class StreakResult(
    val current: Int,
    val best: Int,
    val points: Int,
    val lastDate: String,
    val freezesLeft: Int,
    val freezeUsed: Boolean = false
)

object StreakCalculator {

    private val fmt = DateTimeFormatter.ISO_DATE
    private const val MAX_FREEZES = 3

    fun registerActivity(input: StreakInput, today: LocalDate = LocalDate.now()): StreakResult {
        val todayStr = today.format(fmt)
        val lastStr = input.lastDate

        if (lastStr == todayStr) {
            return StreakResult(input.current, input.best, input.points + 2, todayStr, input.freezesLeft)
        }

        if (lastStr.isNullOrBlank()) {
            return StreakResult(1, maxOf(1, input.best), input.points + 10, todayStr, input.freezesLeft)
        }

        val last = LocalDate.parse(lastStr, fmt)
        val gap = ChronoUnit.DAYS.between(last, today)

        if (gap <= 0) {
            return StreakResult(input.current, input.best, input.points + 2, todayStr, input.freezesLeft)
        }

        if (gap == 1L) {
            return consecutive(input, todayStr)
        }

        // 1 día perdido (gap == 2) y hay freeze: mantener racha consumiendo freeze.
        if (gap == 2L && input.freezesLeft > 0) {
            val continued = consecutive(input.copy(freezesLeft = input.freezesLeft - 1), todayStr)
            return continued.copy(freezesLeft = continued.freezesLeft, freezeUsed = true)
        }

        return StreakResult(1, input.best, input.points + 10, todayStr, input.freezesLeft)
    }

    private fun consecutive(input: StreakInput, todayStr: String): StreakResult {
        val current = input.current + 1
        var points = input.points + 10
        var freezes = input.freezesLeft
        if (current % 7 == 0) {
            points += 50
            if (freezes < MAX_FREEZES) freezes += 1
        }
        return StreakResult(current, maxOf(current, input.best), points, todayStr, freezes)
    }

    fun levelFor(points: Int): String = Levels.forPoints(points).name
}
