package com.sipe.app.ui.screens.plan

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


// ---------- Planificador: Semestre / Notas ----------

@Composable
fun PlanScreen(
    grades: List<Grade>,
    courses: List<Course>,
    tasks: List<TaskItem>,
    averages: Map<String, Double>,
    onAddGrade: (Grade) -> Unit,
    onDeleteGrade: (Grade) -> Unit,
    onOpenClasses: () -> Unit,
    onOpenClass: (Long) -> Unit,
    onToggleTask: (Long, Boolean) -> Unit,
    onDeleteTask: (TaskItem) -> Unit
) {
    var tab by remember { mutableIntStateOf(0) }
    val courseOptions = remember(courses) { courses.map { it.name }.distinct() }
    var gCourse by remember(courseOptions) { mutableStateOf(courseOptions.firstOrNull() ?: "General") }
    var gCourseMenu by remember { mutableStateOf(false) }
    var gLabel by remember { mutableStateOf("Parcial 1") }
    var gValue by remember { mutableStateOf("4.0") }
    var gWeight by remember { mutableStateOf("25") }
    val general = if (averages.isEmpty()) null else averages.values.average()
    val today = LocalDate.now()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Planificador", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        TabRow(selectedTabIndex = tab) {
            Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Semestre") })
            Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Notas") })
        }
        Spacer(Modifier.height(8.dp))
        when (tab) {
            0 -> LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { Text("Actual", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
                item {
                    Card(onClick = onOpenClasses, modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("Semestre en curso", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("${courses.size} asignaturas • Promedio ${general?.let { String.format("%.2f", it) } ?: "—"}")
                            }
                            Icon(Icons.Filled.ChevronRight, contentDescription = null)
                        }
                    }
                }
                item { SectionHeader("Estado general") }
                item {
                    val passing = averages.count { it.value >= 3.0 }
                    val overdue = tasks.filter { !it.done && (taskDate(it)?.isBefore(today) == true) }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard(Modifier.weight(1f), Icons.Filled.Star, general?.let { String.format("%.1f", it) } ?: "—", "Promedio") {}
                        StatCard(Modifier.weight(1f), Icons.Filled.CheckCircle, "$passing/${averages.size}", "Aprobando") {}
                        StatCard(Modifier.weight(1f), Icons.Filled.Warning, "${overdue.size}", "Vencidas") {}
                    }
                }
                item {
                    val overdue = tasks.filter { !it.done && (taskDate(it)?.isBefore(today) == true) }
                    if (overdue.isNotEmpty()) SectionHeader("Vencidas: ponte al día (${overdue.size})")
                }
                items(tasks.filter { !it.done && (taskDate(it)?.isBefore(today) == true) }) { t ->
                    TaskCard(t, onToggleTask, onDeleteTask)
                }
                item { SectionHeader("Por materia") }
                if (courses.isEmpty()) {
                    item { EmptyState("Sin materias este semestre.") }
                }
                items(courses) { c ->
                    val avg = averages[c.name]
                    val pend = tasks.count { !it.done && it.courseName == c.name }
                    val next = tasks.filter { !it.done && it.courseName == c.name }
                        .mapNotNull { taskDate(it) }.minOrNull()
                    Card(onClick = { onOpenClass(c.id) }, modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(14.dp).clip(CircleShape).background(courseDot(c.colorHex)))
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(c.name, fontWeight = FontWeight.SemiBold)
                                Text(
                                    if (avg == null) "Sin notas • $pend pendientes"
                                    else "$pend pendientes • próxima: ${next?.toString() ?: "—"}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            if (avg != null) Pill(String.format("%.2f", avg), gradeColor(avg))
                            else Pill("—", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
                            Icon(Icons.Filled.ChevronRight, contentDescription = null)
                        }
                    }
                }
            }
            1 -> LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Grade, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Promedio general", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    general?.let { String.format("%.2f", it) } ?: "—",
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (general == null) MaterialTheme.colorScheme.onPrimaryContainer else gradeColor(general)
                                )
                                if (general != null) Text(
                                    if (general >= 3.0) "Vas aprobando todo ✅" else "En riesgo ⚠️",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
                if (averages.isEmpty()) {
                    item { EmptyState("Aún no hay notas. Agrega la primera abajo.") }
                }
                averages.forEach { (course, avg) ->
                    val courseGrades = grades.filter { it.courseName == course }
                    item {
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(course, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    Pill(String.format("%.2f", avg), gradeColor(avg))
                                }
                                LinearProgressIndicator(progress = { (avg / 5.0).toFloat().coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth())
                                courseGrades.forEach { g ->
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                        Column(Modifier.weight(1f)) {
                                            Text(g.label, fontWeight = FontWeight.SemiBold)
                                            Text("Peso ${(g.weight * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
                                        }
                                        Pill(String.format("%.1f", g.value), gradeColor(g.value))
                                        IconButton(onClick = { onDeleteGrade(g) }) {
                                            Icon(Icons.Filled.Delete, contentDescription = "Quitar nota", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Agregar nota", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Elige la materia: la nota se suma a su promedio.", style = MaterialTheme.typography.bodySmall)
                            Box {
                                OutlinedTextField(
                                    gCourse, {}, readOnly = true, label = { Text("Materia") },
                                    trailingIcon = {
                                        IconButton(onClick = { gCourseMenu = true }) {
                                            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Elegir materia")
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                DropdownMenu(expanded = gCourseMenu, onDismissRequest = { gCourseMenu = false }) {
                                    courseOptions.forEach { o ->
                                        DropdownMenuItem(text = { Text(o) }, onClick = { gCourse = o; gCourseMenu = false })
                                    }
                                }
                            }
                            OutlinedTextField(gLabel, { gLabel = it }, label = { Text("¿Qué calificaron? Ej: Parcial 1") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(gValue, { gValue = it }, label = { Text("Nota (0–5)") }, modifier = Modifier.weight(1f), singleLine = true)
                                OutlinedTextField(gWeight, { gWeight = it.filter { c -> c.isDigit() } }, label = { Text("Peso %") }, modifier = Modifier.weight(1f), singleLine = true)
                            }
                            Button(onClick = {
                                val v = gValue.toDoubleOrNull() ?: return@Button
                                if (v < 0 || v > 5) return@Button
                                val w = (gWeight.toDoubleOrNull() ?: 25.0) / 100.0
                                onAddGrade(Grade(courseName = gCourse.trim(), label = gLabel.trim(), value = v, weight = w.coerceIn(0.01, 1.0)))
                                gLabel = ""
                            }, modifier = Modifier.fillMaxWidth()) { Text("Guardar nota") }
                        }
                    }
                }
            }
        }
    }
}
