package com.sipe.app.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class StreakCalculatorTest {

    @Test
    fun `primer dia inicia racha en 1`() {
        val r = StreakCalculator.registerActivity(
            StreakInput(0, 0, 0, null, 1), LocalDate.of(2026, 9, 30)
        )
        assertEquals(1, r.current)
        assertEquals(1, r.best)
        assertEquals(10, r.points)
        assertEquals("2026-09-30", r.lastDate)
    }

    @Test
    fun `mismo dia no cambia racha y suma 2 puntos`() {
        val r = StreakCalculator.registerActivity(
            StreakInput(4, 5, 100, "2026-09-30", 1), LocalDate.of(2026, 9, 30)
        )
        assertEquals(4, r.current)
        assertEquals(102, r.points)
        assertFalse(r.freezeUsed)
    }

    @Test
    fun `dia consecutivo incrementa y da bonus cada 7`() {
        val r = StreakCalculator.registerActivity(
            StreakInput(6, 6, 60, "2026-09-29", 1), LocalDate.of(2026, 9, 30)
        )
        assertEquals(7, r.current)
        assertEquals(7, r.best)
        assertEquals(120, r.points) // 60 + 10 + 50 bonus
        assertEquals(2, r.freezesLeft) // +1 freeze por semana completa
    }

    @Test
    fun `un dia perdido con freeze mantiene la racha`() {
        val r = StreakCalculator.registerActivity(
            StreakInput(5, 8, 50, "2026-09-28", 2), LocalDate.of(2026, 9, 30)
        )
        assertEquals(6, r.current)
        assertEquals(8, r.best)
        assertEquals(1, r.freezesLeft)
        assertTrue(r.freezeUsed)
    }

    @Test
    fun `un dia perdido sin freeze reinicia`() {
        val r = StreakCalculator.registerActivity(
            StreakInput(5, 8, 50, "2026-09-28", 0), LocalDate.of(2026, 9, 30)
        )
        assertEquals(1, r.current)
        assertEquals(8, r.best)
        assertEquals(60, r.points)
        assertFalse(r.freezeUsed)
    }

    @Test
    fun `brecha larga reinicia aunque haya freeze`() {
        val r = StreakCalculator.registerActivity(
            StreakInput(9, 9, 200, "2026-09-20", 2), LocalDate.of(2026, 9, 30)
        )
        assertEquals(1, r.current)
        assertEquals(2, r.freezesLeft)
    }

    @Test
    fun `cruza cambio de mes y anio`() {
        val finMes = StreakCalculator.registerActivity(
            StreakInput(3, 3, 30, "2026-09-30", 1), LocalDate.of(2026, 10, 1)
        )
        assertEquals(4, finMes.current)

        val finAnio = StreakCalculator.registerActivity(
            StreakInput(3, 3, 30, "2026-12-31", 1), LocalDate.of(2027, 1, 1)
        )
        assertEquals(4, finAnio.current)
    }

    @Test
    fun niveles() {
        assertEquals("Novato", StreakCalculator.levelFor(0))
        assertEquals("En racha", StreakCalculator.levelFor(50))
        assertEquals("Avanzado", StreakCalculator.levelFor(200))
        assertEquals("Maestro", StreakCalculator.levelFor(500))
        assertEquals("Leyenda SIPE", StreakCalculator.levelFor(1000))
    }
}
