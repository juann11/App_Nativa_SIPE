package com.sipe.app.ui.screens.settings

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
import androidx.compose.foundation.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.res.painterResource
import com.sipe.app.R
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
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Ajustes", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        item {
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
        }
        item {
            Text("Mi nivel", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
        item {
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
        }
        item {
        Text("Todos los niveles", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
        item {
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
        }
        item {
        Text("Apariencia", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
        item {
        Card(Modifier.fillMaxWidth()) {
            Row(
                Modifier.fillMaxWidth()
                    .heightIn(min = 64.dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text("Tema oscuro", fontWeight = FontWeight.SemiBold)
                    Text(
                        if (dark) "Activado" else "Desactivado",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(Modifier.width(16.dp))
                Switch(checked = dark, onCheckedChange = onDark)
            }
        }
        }
        item {
        Text("General", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
        item {
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(4.dp)) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Constancia diaria", fontWeight = FontWeight.SemiBold)
                        Text("Abrir la app o completar una tarea cada día mantiene viva tu racha.", style = MaterialTheme.typography.bodySmall)
                    }
                }
                TextButton(onClick = onAbout, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.Info, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Créditos e información  •  SIPE v1.0.0")
                }
            }
        }
        }
        item {
        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.Logout, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Cerrar sesión")
        }
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
                Image(
                    painter = painterResource(id = R.drawable.ic_logo_mark),
                    contentDescription = "Logo SIPE",
                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(14.dp))
                )
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
                Text("La app", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Hoy, Calendario, Tareas, Plan y Ajustes, con rachas y niveles que premian tu constancia.")
                Text("Tus datos se guardan en este dispositivo.", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
