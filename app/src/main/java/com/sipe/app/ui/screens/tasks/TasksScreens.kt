package com.sipe.app.ui.screens.tasks

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


// ---------- Tareas: Actuales / Completadas + orden ----------

private enum class TaskSort(val label: String) {
    DATE("Fecha de entrega"), COURSE("Asignatura"), PRIORITY("Prioridad")
}



private fun priorityRank(p: String) = when (p.uppercase()) {
    "ALTA" -> 0
    "MEDIA" -> 1
    else -> 2
}



@Composable
fun TasksScreen(
    tasks: List<TaskItem>,
    onToggle: (Long, Boolean) -> Unit,
    onDelete: (TaskItem) -> Unit,
    onAdd: () -> Unit
) {
    var tab by remember { mutableIntStateOf(0) } // 0 Actuales, 1 Completadas
    var sort by remember { mutableStateOf(TaskSort.DATE) }
    val pending = remember(tasks, sort) {
        val list = tasks.filter { !it.done }
        when (sort) {
            TaskSort.DATE -> list.sortedWith(compareBy({ taskDate(it) ?: LocalDate.MAX }, { priorityRank(it.priority) }))
            TaskSort.COURSE -> list.sortedWith(compareBy({ it.courseName }, { taskDate(it) ?: LocalDate.MAX }))
            TaskSort.PRIORITY -> list.sortedWith(compareBy({ priorityRank(it.priority) }, { taskDate(it) ?: LocalDate.MAX }))
        }
    }
    val done = remember(tasks) { tasks.filter { it.done }.sortedByDescending { it.id } }
    val groups = remember(pending, sort) {
        if (sort == TaskSort.DATE) pending.groupBy { taskDate(it) }.toSortedMap(compareBy { it ?: LocalDate.MAX })
        else mapOf(null to pending)
    }

    Scaffold(
        floatingActionButton = {
            androidx.compose.material3.ExtendedFloatingActionButton(
                onClick = onAdd,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nueva") }
            )
        }
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(16.dp).padding(pad)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Tareas", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("${pending.size} actuales", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(8.dp))
            TabRow(selectedTabIndex = tab) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Actuales (${pending.size})") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Completadas") })
            }
            Spacer(Modifier.height(8.dp))
            if (tab == 0) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaskSort.entries.forEach { s ->
                        FilterChip(selected = sort == s, onClick = { sort = s }, label = { Text(s.label) })
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (tab == 0) {
                    if (pending.isEmpty()) {
                        item { EmptyState("Sin tareas actuales. ¡Crea una con el botón +!") }
                    }
                    groups.forEach { (date, list) ->
                        if (sort == TaskSort.DATE) {
                            item {
                                Text(
                                    groupLabel(date),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        items(list) { t -> TaskCard(t, onToggle, onDelete) }
                    }
                } else {
                    if (done.isEmpty()) {
                        item { EmptyState("Aún no completas tareas. Marca ✅ al terminar una.") }
                    }
                    items(done) { t -> TaskCard(t, onToggle, onDelete) }
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormScreen(courses: List<Course>, onSave: (TaskItem) -> Unit, onCancel: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    val options = remember(courses) { (courses.map { it.name }.distinct() + "General").distinct() }
    var course by remember(options) { mutableStateOf(options.firstOrNull() ?: "General") }
    var expanded by remember { mutableStateOf(false) }
    var date by remember { mutableStateOf(LocalDate.now().plusDays(2).toString()) }
    var showPicker by remember { mutableStateOf(false) }
    var priority by remember { mutableStateOf("MEDIA") }
    val valid = title.isNotBlank()

    if (showPicker) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = runCatching { LocalDate.parse(date).toEpochDay() * 86_400_000 }.getOrDefault(System.currentTimeMillis())
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis ->
                        date = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault()).toLocalDate().toString()
                    }
                    showPicker = false
                }) { Text("Listo") }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Cancelar") } }
        ) { DatePicker(state) }
    }

    Column(
        Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Nueva tarea", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Completarla te suma racha y puntos.", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(title, { title = it }, label = { Text("¿Qué tienes que hacer?*") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(desc, { desc = it }, label = { Text("Detalles (opcional)") }, modifier = Modifier.fillMaxWidth())
        Box {
            OutlinedTextField(
                course, {}, readOnly = true, label = { Text("Materia") },
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = "Elegir materia")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { o ->
                    DropdownMenuItem(text = { Text(o) }, onClick = { course = o; expanded = false })
                }
            }
        }
        OutlinedTextField(
            date, {}, readOnly = true, label = { Text("Fecha de entrega") },
            trailingIcon = {
                IconButton(onClick = { showPicker = true }) {
                    Icon(Icons.Filled.CalendarToday, contentDescription = "Elegir fecha")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Text("Prioridad", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("ALTA", "MEDIA", "BAJA").forEach { p ->
                FilterChip(selected = priority == p, onClick = { priority = p }, label = { Text(p) })
            }
        }
        Button(
            onClick = { if (valid) onSave(TaskItem(title = title.trim(), description = desc.trim(), courseName = course.trim(), dueDate = date.trim(), priority = priority)) },
            enabled = valid,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Guardar tarea") }
        OutlinedButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) { Text("Cancelar") }
    }
}
