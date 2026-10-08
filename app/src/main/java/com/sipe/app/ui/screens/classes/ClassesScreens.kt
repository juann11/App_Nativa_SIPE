package com.sipe.app.ui.screens.classes

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


// ---------- LISTADO Clases ----------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassesScreen(courses: List<Course>, onDetail: (Long) -> Unit, onBack: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                }
                Column {
                    Text("Horario de clases", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("${courses.size} materias este semestre", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        if (courses.isEmpty()) {
            item { EmptyState("No hay materias registradas.") }
        }
        items(courses) { c ->
            Card(onClick = { onDetail(c.id) }, modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(14.dp).clip(CircleShape).background(courseDot(c.colorHex)))
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(c.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("${c.dayOfWeek} ${c.startTime}–${c.endTime} • ${c.classroom}")
                        Text("${c.professor} • ${c.credits} créd.", style = MaterialTheme.typography.bodySmall)
                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = null)
                }
            }
        }
    }
}



@Composable
fun ClassDetailScreen(course: Course?, tasks: List<TaskItem> = emptyList(), onBack: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (course == null) {
            item { Text("Materia no encontrada") }
        } else {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(18.dp).clip(CircleShape).background(courseDot(course.colorHex)))
                    Spacer(Modifier.width(8.dp))
                    Text(course.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }
            }
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        DetailRow("Profesor", course.professor)
                        DetailRow("Salón", course.classroom)
                        DetailRow("Horario", "${course.dayOfWeek} ${course.startTime} – ${course.endTime}")
                        DetailRow("Créditos", "${course.credits}")
                    }
                }
            }
            item {
                Text("Entregas de esta materia", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            val pendingMine = tasks.filter { it.courseName == course.name && !it.done }
            val doneMine = tasks.filter { it.courseName == course.name && it.done }
            if (pendingMine.isEmpty() && doneMine.isEmpty()) {
                item { EmptyState("Sin tareas en ${course.name}. Crea una desde Tareas eligiendo esta materia.") }
            } else {
                if (pendingMine.isNotEmpty()) {
                    item {
                        Text("Pendientes (${pendingMine.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                    items(pendingMine) { t ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Pill(t.priority, priorityColors(t.priority).first)
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(t.title, fontWeight = FontWeight.SemiBold)
                                    Text("Entrega: ${t.dueDate.ifBlank { "sin fecha" }}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
                if (doneMine.isNotEmpty()) {
                    item {
                        Text("Entregadas ✅ (${doneMine.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                    items(doneMine) { t ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = gradeColor(4.5))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    t.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            }
                        }
                    }
                }
            }
        }
        item {
            Spacer(Modifier.height(8.dp))
        }
        item {
            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Volver") }
        }
    }
}



@Composable
fun DetailRow(label: String, value: String) {
    Row {
        Text("$label: ", fontWeight = FontWeight.Bold)
        Text(value)
    }
}



// ---------- Detalle curso loader ----------

@Composable
fun ClassDetailLoader(vm: MainViewModel, id: Long, onBack: () -> Unit) {
    var course by remember { mutableStateOf<Course?>(null) }
    val tasks by vm.tasks.collectAsState()
    LaunchedEffect(id) { course = vm.course(id) }
    ClassDetailScreen(course, tasks, onBack)
}
