package com.sipe.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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

private val ES = Locale("es", "ES")

// ---------- Ayudas compartidas ----------

fun dowKey(d: DayOfWeek): String = when (d) {
    DayOfWeek.MONDAY -> "LUN"
    DayOfWeek.TUESDAY -> "MAR"
    DayOfWeek.WEDNESDAY -> "MIE"
    DayOfWeek.THURSDAY -> "JUE"
    DayOfWeek.FRIDAY -> "VIE"
    DayOfWeek.SATURDAY -> "SAB"
    DayOfWeek.SUNDAY -> "DOM"
}

fun taskDate(t: TaskItem): LocalDate? = runCatching { LocalDate.parse(t.dueDate) }.getOrNull()

fun groupLabel(d: LocalDate?): String = d?.let {
    "${it.dayOfWeek.getDisplayName(TextStyle.SHORT, ES).uppercase()} ${it.dayOfMonth}/${it.monthValue}/${it.year}"
} ?: "Sin fecha"

fun coursePalette(): List<Color> = listOf(
    Color(0xFF1B5E20), Color(0xFF1565C0), Color(0xFF6A1B9A),
    Color(0xFFEF6C00), Color(0xFF00695C), Color(0xFFC62828)
)

fun courseDotFor(t: TaskItem): Color {
    val i = (t.courseName.hashCode() and Int.MAX_VALUE) % coursePalette().size
    return coursePalette()[i]
}

@Composable
fun SectionHeader(title: String, actionLabel: String? = null, onAction: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        if (actionLabel != null && onAction != null) {
            TextButton(onClick = onAction) { Text(actionLabel) }
        }
    }
}

@Composable
fun Pill(text: String, container: Color, content: Color = Color.White) {
    Surface(color = container, contentColor = content, shape = RoundedCornerShape(12.dp)) {
        Text(text, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
    }
}

@Composable
fun EmptyState(text: String) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Text(text, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(16.dp))
    }
}

fun priorityColors(p: String): Pair<Color, Color> = when (p.uppercase()) {
    "ALTA" -> Color(0xFFC62828) to Color.White
    "MEDIA" -> Color(0xFFEF6C00) to Color.White
    else -> Color(0xFF2E7D32) to Color.White
}

fun gradeColor(v: Double): Color = when {
    v >= 4.0 -> Color(0xFF2E7D32)
    v >= 3.0 -> Color(0xFFEF6C00)
    else -> Color(0xFFC62828)
}

fun courseDot(hex: String): Color =
    runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrDefault(Color(0xFF1B5E20))

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

@Composable
private fun StatCard(mod: Modifier, icon: ImageVector, value: String, label: String, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = mod) {
        Column(
            Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodySmall)
        }
    }
}

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
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (course == null) {
            Text("Materia no encontrada")
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(18.dp).clip(CircleShape).background(courseDot(course.colorHex)))
                Spacer(Modifier.width(8.dp))
                Text(course.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    DetailRow("Profesor", course.professor)
                    DetailRow("Salón", course.classroom)
                    DetailRow("Horario", "${course.dayOfWeek} ${course.startTime} – ${course.endTime}")
                    DetailRow("Créditos", "${course.credits}")
                }
            }
            Text("Entregas de esta materia", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            val pendingMine = tasks.filter { it.courseName == course.name && !it.done }
            val doneMine = tasks.filter { it.courseName == course.name && it.done }
            if (pendingMine.isEmpty() && doneMine.isEmpty()) {
                EmptyState("Sin tareas en ${course.name}. Crea una desde Tareas eligiendo esta materia.")
            } else {
                if (pendingMine.isNotEmpty()) {
                    Text("Pendientes (${pendingMine.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    pendingMine.forEach { t ->
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
                        Spacer(Modifier.height(6.dp))
                    }
                }
                if (doneMine.isNotEmpty()) {
                    Text("Entregadas ✅ (${doneMine.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    doneMine.forEach { t ->
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
                        Spacer(Modifier.height(6.dp))
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Volver") }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row {
        Text("$label: ", fontWeight = FontWeight.Bold)
        Text(value)
    }
}

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

@Composable
fun TaskCard(t: TaskItem, onToggle: (Long, Boolean) -> Unit, onDelete: (TaskItem) -> Unit) {
    val (bg, fg) = priorityColors(t.priority)
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(t.done, { onToggle(t.id, !t.done) })
                Box(Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(courseDotFor(t)))
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(t.courseName, color = courseDotFor(t), style = MaterialTheme.typography.bodySmall)
                    Text(
                        t.title,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = if (t.done) TextDecoration.LineThrough else null
                    )
                    if (t.description.isNotBlank()) Text(t.description, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Pill(t.priority, bg, fg)
                        Pill(t.dueDate.ifBlank { "Sin fecha" }, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                IconButton(onClick = { onDelete(t) }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
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

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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

// ---------- CONFIGURACIÓN ----------

@Composable
fun SettingsScreen(
    name: String,
    email: String,
    points: Int,
    dark: Boolean,
    onDark: (Boolean) -> Unit,
    onLogout: () -> Unit,
    onAbout: () -> Unit
) {
    val myLevel = Levels.forPoints(points)
    val next = Levels.nextAfter(points)
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Ajustes", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Card(Modifier.fillMaxWidth()) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.AccountCircle, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (email.isNotBlank()) Text(email, style = MaterialTheme.typography.bodyMedium)
                    else Text("Cuenta local en este dispositivo", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        Text("Mi nivel", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(myLevel.emoji, fontSize = 48.sp)
                Text(myLevel.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("$points pts", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { Levels.progress(points) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    if (next != null) "${Levels.pointsToNext(points)} pts para ${next.emoji} ${next.name}"
                    else "Nivel máximo 👑",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }
        Text("Todos los niveles", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(4.dp)) {
                Levels.all.forEach { l ->
                    val unlocked = points >= l.minPoints
                    Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(l.emoji, fontSize = 28.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(l.name, fontWeight = FontWeight.SemiBold)
                            Text(
                                if (unlocked) l.phrase else "Se desbloquea con ${l.minPoints} pts",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Text(
                            if (unlocked) "✅" else "🔒",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
        Text("Apariencia", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Card(Modifier.fillMaxWidth()) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Text("Tema oscuro", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                Switch(dark, onDark)
            }
        }
        Text("General", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(4.dp)) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Recordatorios", fontWeight = FontWeight.SemiBold)
                        Text("Aviso diario para no perder tu racha (próxima versión).", style = MaterialTheme.typography.bodySmall)
                    }
                }
                TextButton(onClick = onAbout, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.Info, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Créditos e información  •  SIPE v1.0.0")
                }
            }
        }
        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.Logout, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Cerrar sesión")
        }
    }
}

// ---------- CRÉDITOS ----------

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
            }
            Text("Créditos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.School, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("SIPE v1.0.0", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Sistema Integrado de Planificación Estudiantil")
                }
            }
        }
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text("Equipo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Text("• Juan José Ospina Arroyave")
                Text("Ingeniería de Sistemas e Informática", style = MaterialTheme.typography.bodySmall)
            }
        }
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Tecnología", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Kotlin + Jetpack Compose + Material 3")
                Text("MVVM + Room + DataStore", style = MaterialTheme.typography.bodySmall)
                Text("Login, Registro, Hoy, Calendario, Tareas, Plan, Ajustes, Créditos + rachas.", style = MaterialTheme.typography.bodySmall)
            }
        }
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
