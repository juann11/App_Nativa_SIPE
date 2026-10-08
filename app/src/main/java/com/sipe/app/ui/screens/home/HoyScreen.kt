package com.sipe.app.ui.screens.home

import com.sipe.app.ui.screens.components.*

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sipe.app.data.Course
import com.sipe.app.data.Grade
import com.sipe.app.data.TaskItem
import com.sipe.app.domain.Levels
import com.sipe.app.domain.StreakCalculator
import com.sipe.app.ui.MainViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

import com.sipe.app.ui.screens.components.*

// ---------- HOY ----------

@Composable
fun HoyScreen(
    vm: MainViewModel,
    onOpenTasks: () -> Unit,
    onOpenClasses: () -> Unit,
    onOpenPlan: () -> Unit,
    onNewTask: () -> Unit,
    onOpenClass: (Long) -> Unit
) {
    val name by vm.userName.collectAsState()
    val streak by vm.streak.collectAsState()
    val courses by vm.courses.collectAsState()
    val tasks by vm.tasks.collectAsState()
    val grades by vm.grades.collectAsState()
    val today = LocalDate.now()
    val todayLabel = "${today.dayOfWeek.getDisplayName(TextStyle.FULL, ES).replaceFirstChar { it.uppercase() }} ${today.dayOfMonth} de ${today.month.getDisplayName(TextStyle.FULL, ES)}"

    val myClasses = remember(today, courses) {
        courses.filter { it.dayOfWeek.uppercase() == dowKey(today.dayOfWeek) }.sortedBy { it.startTime }
    }
    val myTasks = remember(today, tasks) {
        tasks.filter { !it.done && taskDate(it) == today }
    }
    val averages = remember(grades) { vm.averages() }
    val general = if (averages.isEmpty()) null else averages.values.average()
    val free = myClasses.isEmpty() && myTasks.isEmpty()

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Hola, $name", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(todayLabel, style = MaterialTheme.typography.bodyMedium)
                }
                Pill(StreakCalculator.levelFor(streak.points), MaterialTheme.colorScheme.primary)
            }
        }
        item {
            val level = Levels.forPoints(streak.points)
            val next = Levels.nextAfter(streak.points)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(level.emoji, fontSize = 56.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${streak.current} ${if (streak.current == 1) "día" else "días"} de racha",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                    Pill("${level.name} • ${streak.points} pts", MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        level.phrase,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { Levels.progress(streak.points) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        if (next != null) "Te faltan ${Levels.pointsToNext(streak.points)} pts para ${next.emoji} ${next.name}"
                        else "Nivel máximo. ¡Eres leyenda! 👑",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("🏆 Récord ${streak.best}", style = MaterialTheme.typography.bodySmall)
                        Text("   •   ", style = MaterialTheme.typography.bodySmall)
                        Text("❄️ ${streak.freezesLeft} ${if (streak.freezesLeft == 1) "congelamiento" else "congelamientos"}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(Modifier.weight(1f), Icons.Filled.CheckCircle, "${tasks.count { !it.done }}", "Pendientes", onOpenTasks)
                StatCard(Modifier.weight(1f), Icons.Filled.School, "${courses.size}", "Materias", onOpenClasses)
                StatCard(Modifier.weight(1f), Icons.Filled.Star, general?.let { String.format("%.1f", it) } ?: "—", "Promedio", onOpenPlan)
            }
        }
        if (free) {
            item { FriendlyEmpty("¿Por qué no te tomas un día libre?", "¡Diviértete!") }
        } else {
            item { SectionHeader("Clases de hoy (${myClasses.size})", "Horario", onOpenClasses) }
            if (myClasses.isEmpty()) {
                item { EmptyState("Sin clases hoy.") }
            } else {
                items(myClasses) { c ->
                    Card(onClick = { onOpenClass(c.id) }, modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(courseDot(c.colorHex)))
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(c.name, fontWeight = FontWeight.SemiBold)
                                Text("${c.startTime}–${c.endTime} • ${c.classroom}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
            item { SectionHeader("Para entregar hoy (${myTasks.size})", "Ver todas", onOpenTasks) }
            if (myTasks.isEmpty()) {
                item { EmptyState("Nada que entregar hoy.") }
            } else {
                items(myTasks) { t -> TaskCard(t, { id, d -> vm.toggleTask(id, d) }, { vm.deleteTask(it) }) }
            }
        }
        item {
            Button(onClick = onNewTask, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Nueva tarea")
            }
        }
    }
}