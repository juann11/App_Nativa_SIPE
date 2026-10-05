# Arquitectura, datos y rachas

## Estructura de paquetes
```
com.<equipo>.organizador/
├── data/        local (Room: entities, dao, database), repository impl, datastore
├── domain/      models, repository interfaces, usecases, streak/StreakCalculator
├── ui/          theme, navigation, components, screens/<feature>/(Screen, ViewModel, UiState)
└── di/          módulos Hilt
```
Screens: auth (login, registro), home (menú/resumen), tasks (lista, detalle, formulario), schedule (calendario y clases), grades (notas), semester (planificación), streak, settings, credits.

## Modelo de datos (Room)
- User(id, name, email, passwordHash, createdAt)
- Course(id, name, teacher, room, color, credits, semesterId)
- ClassSession(id, courseId, dayOfWeek, startTime, endTime)
- Task(id, courseId?, title, description, dueAt, priority, done, completedAt?)
- Grade(id, courseId, name, weightPercent, score?, maxScore)
- Semester(id, name, startDate, endDate)
- StreakState(userId, currentStreak, bestStreak, lastActiveDate, freezesLeft)

Promedio ponderado del curso: sum(score/maxScore * weight) / sum(weight de notas con score). Proyección: nota mínima necesaria en lo que falta para alcanzar el objetivo.

## Sistema de rachas
Regla: un día cuenta como activo si el usuario completa al menos una tarea o registra su plan del día. Lógica pura sobre fechas locales:
- Si hoy == lastActiveDate: no cambia.
- Si hoy == lastActiveDate + 1 día: current += 1.
- Si hoy > lastActiveDate + 1: si hay freeze disponible y la brecha es de 1 día perdido, consumirlo y mantener; si no, current = 1.
- best = max(best, current).
Usar `LocalDate` con la zona del dispositivo; guardar solo la fecha, no la hora. Escribir pruebas unitarias del calculador (mismo día, día consecutivo, brecha, cambio de mes/año). Notificación diaria con WorkManager si la racha está en riesgo.

## Navegación
Bottom navigation con: Inicio, Calendario, Tareas, Notas, Más (semestre, configuración, créditos). Grafo de auth separado; sesión persistida en DataStore.

## Pantalla de créditos
Nombre, código, programa, universidad, versión de la app y correo de cada integrante.

## Buenas prácticas
- Un ViewModel por pantalla, UiState inmutable, eventos como funciones.
- Repositorios devuelven Flow; nada de acceso a Room desde la UI.
- Strings en `strings.xml` (español) para poder localizar.
- Contraseñas: nunca en texto plano; si son locales, hash con salt (por ejemplo PBKDF2 de javax.crypto).
- Ramas por feature y PR revisados por otro integrante.
