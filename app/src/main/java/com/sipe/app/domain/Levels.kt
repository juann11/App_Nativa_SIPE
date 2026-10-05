package com.sipe.app.domain

/**
 * Niveles SIPE: cada nivel tiene mascota animal y frase motivadora.
 * Fuente única de verdad para [StreakCalculator.levelFor].
 */
data class Level(
    val name: String,
    val emoji: String,
    val minPoints: Int,
    val phrase: String
)

object Levels {

    val all = listOf(
        Level("Novato", "🐣", 0, "Todo comienzo cuenta. ¡Suma tu primer día!"),
        Level("En racha", "🐰", 50, "Vas rápido como un conejo. No pares."),
        Level("Avanzado", "🦊", 200, "Astuto como un zorro: estudias con estrategia."),
        Level("Maestro", "🦉", 500, "Sabio como un búho. Otros te siguen."),
        Level("Leyenda SIPE", "🐉", 1000, "Eres leyenda. El dragón del semestre.")
    )

    fun forPoints(points: Int): Level =
        all.filter { points >= it.minPoints }.maxByOrNull { it.minPoints } ?: all.first()

    fun nextAfter(points: Int): Level? =
        all.firstOrNull { it.minPoints > points }

    /** Progreso 0..1 hacia el siguiente nivel (1 si ya es el máximo). */
    fun progress(points: Int): Float {
        val current = forPoints(points)
        val next = nextAfter(points) ?: return 1f
        val span = (next.minPoints - current.minPoints).coerceAtLeast(1)
        return ((points - current.minPoints).toFloat() / span).coerceIn(0f, 1f)
    }

    fun pointsToNext(points: Int): Int {
        val next = nextAfter(points) ?: return 0
        return next.minPoints - points
    }
}
