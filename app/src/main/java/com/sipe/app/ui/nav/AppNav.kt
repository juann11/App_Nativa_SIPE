package com.sipe.app.ui.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sipe.app.SipeApplication
import com.sipe.app.ui.AuthViewModel
import com.sipe.app.ui.MainViewModel
import com.sipe.app.ui.screens.auth.LoginScreen
import com.sipe.app.ui.screens.auth.RegisterScreen
import com.sipe.app.ui.screens.calendar.CalendarScreen
import com.sipe.app.ui.screens.classes.ClassDetailLoader
import com.sipe.app.ui.screens.classes.ClassesScreen
import com.sipe.app.ui.screens.home.HoyScreen
import com.sipe.app.ui.screens.plan.PlanScreen
import com.sipe.app.ui.screens.settings.AboutScreen
import com.sipe.app.ui.screens.settings.SettingsScreen
import com.sipe.app.ui.screens.tasks.TaskFormScreen
import com.sipe.app.ui.screens.tasks.TasksScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val CALENDAR = "calendar"
    const val CLASSES = "classes"
    const val CLASS_DETAIL = "class/{id}"
    const val TASKS = "tasks"
    const val TASK_FORM = "task_form"
    const val PLAN = "plan"
    const val SETTINGS = "settings"
    const val ABOUT = "about"

    fun classDetail(id: Long) = "class/$id"
}

@Composable
fun AppNav(app: SipeApplication, startLogged: Boolean) {
    val nav = rememberNavController()
    val authVm: AuthViewModel = viewModel(factory = AuthViewModel.factory(app))
    val mainVm: MainViewModel = viewModel(factory = MainViewModel.factory(app))

    NavHost(nav, startDestination = if (startLogged) Routes.HOME else Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(authVm, onLoginOk = {
                nav.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
            }, onGoRegister = { nav.navigate(Routes.REGISTER) })
        }
        composable(Routes.REGISTER) {
            RegisterScreen(authVm, onOk = {
                nav.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
            }, onBack = { nav.popBackStack() })
        }
        composable(Routes.HOME) {
            MainScaffold(mainVm, nav, Routes.HOME) {
                HoyScreen(
                    mainVm,
                    onOpenTasks = { nav.navigate(Routes.TASKS) },
                    onOpenClasses = { nav.navigate(Routes.CLASSES) },
                    onOpenPlan = { nav.navigate(Routes.PLAN) },
                    onNewTask = { nav.navigate(Routes.TASK_FORM) },
                    onOpenClass = { nav.navigate(Routes.classDetail(it)) }
                )
            }
        }
        composable(Routes.CALENDAR) {
            val courses by mainVm.courses.collectAsState()
            val tasks by mainVm.tasks.collectAsState()
            MainScaffold(mainVm, nav, Routes.CALENDAR) {
                CalendarScreen(
                    courses = courses,
                    tasks = tasks,
                    onToggleTask = { id, d -> mainVm.toggleTask(id, d) },
                    onAddTask = { nav.navigate(Routes.TASK_FORM) },
                    onOpenClass = { nav.navigate(Routes.classDetail(it)) }
                )
            }
        }
        composable(Routes.CLASSES) {
            val courses by mainVm.courses.collectAsState()
            MainScaffold(mainVm, nav, Routes.CLASSES) {
                ClassesScreen(
                    courses,
                    onDetail = { nav.navigate(Routes.classDetail(it)) },
                    onBack = { nav.popBackStack() }
                )
            }
        }
        composable(Routes.CLASS_DETAIL, arguments = listOf(navArgument("id") { type = NavType.LongType })) {
            val id = it.arguments?.getLong("id") ?: 0L
            ClassDetailLoader(mainVm, id) { nav.popBackStack() }
        }
        composable(Routes.TASKS) {
            val tasks by mainVm.tasks.collectAsState()
            MainScaffold(mainVm, nav, Routes.TASKS) {
                TasksScreen(tasks, onToggle = { id, d -> mainVm.toggleTask(id, d) }, onDelete = { mainVm.deleteTask(it) }, onAdd = { nav.navigate(Routes.TASK_FORM) })
            }
        }
        composable(Routes.TASK_FORM) {
            val courses by mainVm.courses.collectAsState()
            TaskFormScreen(courses, onSave = { mainVm.saveTask(it) { nav.popBackStack() } }, onCancel = { nav.popBackStack() })
        }
        composable(Routes.PLAN) {
            val grades by mainVm.grades.collectAsState()
            val courses by mainVm.courses.collectAsState()
            val tasks by mainVm.tasks.collectAsState()
            MainScaffold(mainVm, nav, Routes.PLAN) {
                PlanScreen(
                    grades, courses, tasks, mainVm.averages(),
                    onAddGrade = { mainVm.saveGrade(it) },
                    onDeleteGrade = { mainVm.deleteGrade(it) },
                    onOpenClasses = { nav.navigate(Routes.CLASSES) },
                    onOpenClass = { nav.navigate(Routes.classDetail(it)) },
                    onToggleTask = { id, d -> mainVm.toggleTask(id, d) },
                    onDeleteTask = { mainVm.deleteTask(it) }
                )
            }
        }
        composable(Routes.SETTINGS) {
            val dark by mainVm.dark.collectAsState()
            val name by mainVm.userName.collectAsState()
            val email by mainVm.userEmail.collectAsState()
            val streak by mainVm.streak.collectAsState()
            MainScaffold(mainVm, nav, Routes.SETTINGS) {
                SettingsScreen(
                    name = name,
                    email = email,
                    points = streak.points,
                    dark = dark,
                    onDark = { mainVm.setDark(it) },
                    onLogout = { mainVm.logout { nav.navigate(Routes.LOGIN) { popUpTo(0) } } },
                    onAbout = { nav.navigate(Routes.ABOUT) }
                )
            }
        }
        composable(Routes.ABOUT) {
            MainScaffold(mainVm, nav, Routes.ABOUT) { AboutScreen(onBack = { nav.popBackStack() }) }
        }
    }
}

private data class BottomItem(val route: String, val label: String, val icon: ImageVector, val badge: Int? = null)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScaffold(
    vm: MainViewModel,
    nav: androidx.navigation.NavHostController,
    current: String,
    content: @Composable () -> Unit
) {
    val pending by vm.pending.collectAsState()
    val items = listOf(
        BottomItem(Routes.HOME, "Hoy", Icons.Filled.Today),
        BottomItem(Routes.CALENDAR, "Calendario", Icons.Filled.CalendarMonth),
        BottomItem(Routes.TASKS, "Tareas", Icons.Filled.Assignment, pending.size.takeIf { it > 0 }),
        BottomItem(Routes.PLAN, "Plan", Icons.Filled.AccountBalance),
        BottomItem(Routes.SETTINGS, "Ajustes", Icons.Filled.Settings)
    )
    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = current == item.route,
                        onClick = { nav.navigate(item.route) { launchSingleTop = true } },
                        icon = {
                            BadgedBox(badge = {
                                if (item.badge != null) Badge { Text("${item.badge}") }
                            }) {
                                Icon(item.icon, contentDescription = item.label)
                            }
                        },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { pad ->
        androidx.compose.foundation.layout.Box(Modifier.padding(pad)) { content() }
    }
}
