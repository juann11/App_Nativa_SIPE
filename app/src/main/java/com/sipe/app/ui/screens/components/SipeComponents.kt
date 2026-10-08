package com.sipe.app.ui.screens.components


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




@Composable
fun StatCard(mod: Modifier, icon: ImageVector, value: String, label: String, onClick: () -> Unit) {
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

@Composable
fun FriendlyEmpty(title: String, subtitle: String) {
    Column(
        Modifier.fillMaxWidth().padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🎠", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
