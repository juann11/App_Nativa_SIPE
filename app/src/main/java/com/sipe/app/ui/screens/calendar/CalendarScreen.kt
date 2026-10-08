package com.sipe.app.ui.screens.calendar

import com.sipe.app.ui.screens.components.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sipe.app.data.Course
import com.sipe.app.data.TaskItem
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private val ES = Locale("es", "ES")

/**
 * Calendario mensual de agenda universitaria: selector Horario/Tareas, botón Hoy,
 * puntos en días con eventos y lista de lo programado el día elegido.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    courses: List<Course>,
    tasks: List<TaskItem>,
    onToggleTask: (Long, Boolean) -> Unit,
    onAddTask: () -> Unit,
    onOpenClass: (Long) -> Unit
) {
    var month by remember { mutableStateOf(YearMonth.now()) }
    var selected by remember { mutableStateOf(LocalDate.now()) }
    var mode by remember { mutableIntStateOf(1) } // 0 Horario, 1 Tareas

    val taskDates = remember(tasks) {
        tasks.filter { !it.done }.mapNotNullTo(mutableSetOf()) { taskDate(it) }
    }
    val classWeekdays = remember(courses) { courses.map { it.dayOfWeek.uppercase() }.toSet() }

    fun hasDot(day: LocalDate): Boolean =
        if (mode == 0) dowKey(day.dayOfWeek) in classWeekdays
        else day in taskDates

    val dayCourses = remember(selected, courses) {
        courses.filter { it.dayOfWeek.uppercase() == dowKey(selected.dayOfWeek) }
            .sortedBy { it.startTime }
    }
    val dayTasks = remember(selected, tasks) {
        tasks.filter { taskDate(it) == selected }.sortedBy { it.done }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        // Barra superior: Hoy | Horario/Tareas | +
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = {
                month = YearMonth.now()
                selected = LocalDate.now()
            }) { Text("Hoy") }
            Spacer(Modifier.weight(1f))
            SingleChoiceSegmentedButtonRow {
                SegmentedButton(selected = mode == 0, onClick = { mode = 0 }, shape = RoundedCornerShape(8.dp, 0.dp, 0.dp, 8.dp)) { Text("Horario") }
                SegmentedButton(selected = mode == 1, onClick = { mode = 1 }, shape = RoundedCornerShape(0.dp, 8.dp, 8.dp, 0.dp)) { Text("Tareas") }
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onAddTask) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar", tint = MaterialTheme.colorScheme.primary)
            }
        }
        // Mes con flechas
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { month = month.minusMonths(1) }) {
                Icon(Icons.Filled.ChevronLeft, contentDescription = "Mes anterior")
            }
            Text(
                "${month.month.getDisplayName(TextStyle.FULL, ES).uppercase()} DE ${month.year}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { month = month.plusMonths(1) }) {
                Icon(Icons.Filled.ChevronRight, contentDescription = "Mes siguiente")
            }
        }
        // Encabezado días (la semana inicia el domingo)
        Row(Modifier.fillMaxWidth()) {
            listOf("d", "l", "m", "m", "j", "v", "s").forEach {
                Text(it, Modifier.weight(1f), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(4.dp))
        // Rejilla del mes
        val first = month.atDay(1)
        val offset = first.dayOfWeek.value % 7 // domingo = 0
        val total = offset + month.lengthOfMonth()
        val rows = (total + 6) / 7
        val today = LocalDate.now()
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            repeat(rows) { r ->
                Row(Modifier.fillMaxWidth()) {
                    repeat(7) { c ->
                        val num = r * 7 + c - offset + 1
                        Box(Modifier.weight(1f).aspectRatio(1f), contentAlignment = Alignment.Center) {
                            if (num in 1..month.lengthOfMonth()) {
                                val day = month.atDay(num)
                                val isSel = day == selected
                                val isToday = day == today
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSel) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                        .clickable {
                                            selected = day
                                            if (YearMonth.from(day) != month) month = YearMonth.from(day)
                                        }
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        "$num",
                                        color = when {
                                            isSel -> MaterialTheme.colorScheme.onPrimaryContainer
                                            isToday -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.onSurface
                                        },
                                        fontWeight = if (isToday || isSel) FontWeight.Bold else null
                                    )
                                    Box(
                                        Modifier.size(5.dp).clip(CircleShape)
                                            .background(if (hasDot(day)) MaterialTheme.colorScheme.tertiary else Color.Transparent)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        // Lista del día elegido
        LazyColumn(Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (mode == 0) {
                if (dayCourses.isEmpty()) {
                    item {
                        FriendlyEmpty(
                            "¿Por qué no te tomas un día libre?",
                            "¡Diviértete!"
                        )
                    }
                }
                items(dayCourses) { cl ->
                    Card(onClick = { onOpenClass(cl.id) }, modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(courseDot(cl.colorHex)))
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(cl.name, color = courseDot(cl.colorHex), fontWeight = FontWeight.SemiBold)
                                Text("${cl.startTime}–${cl.endTime} • ${cl.classroom}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            } else {
                if (dayTasks.isEmpty()) {
                    item {
                        FriendlyEmpty(
                            "Nada que entregar este día",
                            "¡Aprovecha para adelantar!"
                        )
                    }
                }
                items(dayTasks) { t ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(t.done, { onToggleTask(t.id, !t.done) })
                            Box(Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(courseDotFor(t)))
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(t.courseName, color = courseDotFor(t), style = MaterialTheme.typography.bodySmall)
                                Text(t.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

