package com.sipe.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.sipe.app.SipeApplication
import com.sipe.app.data.Course
import com.sipe.app.data.Grade
import com.sipe.app.data.SemesterGoal
import com.sipe.app.data.TaskItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(private val app: SipeApplication) : ViewModel() {
    var error by androidx.compose.runtime.mutableStateOf<String?>(null)
        private set
    var busy by androidx.compose.runtime.mutableStateOf(false)
        private set

    fun login(email: String, pass: String, onOk: (String) -> Unit) {
        viewModelScope.launch {
            busy = true
            app.repository.login(email, pass)
                .onSuccess { onOk(it) }
                .onFailure { error = it.message }
            busy = false
        }
    }

    fun register(name: String, email: String, pass: String, onOk: (String) -> Unit) {
        viewModelScope.launch {
            busy = true
            app.repository.register(name, email, pass)
                .onSuccess { onOk(it) }
                .onFailure { error = it.message }
            busy = false
        }
    }

    fun clearError() { error = null }

    companion object {
        fun factory(app: SipeApplication) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return AuthViewModel(app) as T
            }
        }
    }
}

class MainViewModel(private val app: SipeApplication) : ViewModel() {
    val courses = app.repository.courses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val tasks = app.repository.tasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val pending = app.repository.pendingTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val grades = app.repository.grades()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val goals = app.repository.goals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val streak = app.session.streak
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.sipe.app.data.StreakState())
    val userName = app.session.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Estudiante")
    val userEmail = app.session.userEmail
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    val dark = app.session.dark
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        viewModelScope.launch { app.session.registerDailyActivity() }
    }

    fun toggleTask(id: Long, done: Boolean) {
        viewModelScope.launch { app.repository.toggleTask(id, done) }
    }
    fun deleteTask(t: TaskItem) {
        viewModelScope.launch { app.repository.deleteTask(t) }
    }
    fun saveTask(t: TaskItem, onDone: () -> Unit = {}) {
        viewModelScope.launch { app.repository.saveTask(t); onDone() }
    }
    fun saveGrade(g: Grade, onDone: () -> Unit = {}) {
        viewModelScope.launch { app.repository.saveGrade(g); onDone() }
    }
    fun deleteGrade(g: Grade) {
        viewModelScope.launch { app.repository.deleteGrade(g) }
    }
    fun saveGoal(g: SemesterGoal, onDone: () -> Unit = {}) {
        viewModelScope.launch { app.repository.saveGoal(g); onDone() }
    }
    fun deleteGoal(g: SemesterGoal) {
        viewModelScope.launch { app.repository.deleteGoal(g) }
    }
    fun bumpGoal(g: SemesterGoal, delta: Int) {
        val target = g.target.coerceAtLeast(1)
        val next = (g.progress + delta).coerceIn(0, target)
        viewModelScope.launch { app.repository.saveGoal(g.copy(progress = next)) }
    }
    fun logout(onDone: () -> Unit) {
        viewModelScope.launch { app.session.logout(); onDone() }
    }
    fun setDark(v: Boolean) {
        viewModelScope.launch { app.session.setDark(v) }
    }
    suspend fun course(id: Long): Course? = app.repository.getCourse(id)
    fun averages() = app.repository.averageByCourse(grades.value)

    companion object {
        fun factory(app: SipeApplication) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return MainViewModel(app) as T
            }
        }
    }
}
