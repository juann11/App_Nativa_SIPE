# SIPE — Sistema Integrado de Planificación Estudiantil

SIPE es la app oficial de tu universidad para organizar la vida académica: horario de clases, tareas y recordatorios, notas con promedio ponderado, resumen del semestre y un sistema de rachas que te motiva a estudiar todos los días. Diseñada para que la institución la adopte como su app estudiantil. Funciona 100 % sin internet: tus datos viven en el dispositivo.

## Pantallas

| Pantalla | Ruta | Archivo |
|---|---|---|
| Splash (marca) | arranque | `MainActivity.kt` + `Theme.SIPE.Splash` |
| Login / Registro | `login`, `register` | `ui/screens/auth/AuthScreens.kt` |
| Hoy (agenda del día + racha) | `home` | `ui/screens/home/HoyScreen.kt` |
| Calendario mensual | `calendar` | `ui/screens/calendar/CalendarScreen.kt` |
| Tareas + Nueva tarea | `tasks`, `task_form` | `ui/screens/tasks/TasksScreens.kt` |
| Horario + Detalle de materia | `classes`, `class/{id}` | `ui/screens/classes/ClassesScreens.kt` |
| Planificador (Semestre / Notas) | `plan` | `ui/screens/plan/PlanScreen.kt` |
| Ajustes + Créditos | `settings`, `about` | `ui/screens/settings/SettingsScreens.kt` |
| Componentes compartidos | — | `ui/screens/components/SipeComponents.kt` |

## Arquitectura: MVVM por capas

```
┌──────────── UI (Compose) ────────────┐
│ Screens + Navigation + ViewModels    │  ui/
├──────────── Dominio ─────────────────┤
│ Reglas puras, sin Android            │  domain/  (StreakCalculator, Levels)
├──────────── Datos ───────────────────┤
│ Room + DataStore + repositorio       │  data/
└──────────────────────────────────────┘
```

- **Regla de dependencias:** `ui → domain, data`. La UI nunca toca la base de datos: solo `Flow` del repositorio y `StateFlow` de los ViewModels.
- **Un ViewModel por área** (`AuthViewModel`, `MainViewModel`) con estado inmutable observado por Compose.
- **Dominio testeable en JVM:** la lógica de rachas no depende de Android y tiene 8 tests unitarios (`StreakCalculatorTest`).
- **Navegación:** grafo separado para autenticación; barra inferior con Hoy, Calendario, Tareas (badge de pendientes), Plan y Ajustes.

## Stack tecnológico

| Pieza | Versión |
|---|---|
| Kotlin | 2.0.20 (JVM 17) |
| Jetpack Compose BOM / Material 3 | 2024.10.00 |
| Navigation Compose | 2.7.7 |
| Room (KSP) | 2.6.1 |
| DataStore Preferences | 1.1.1 |
| Coroutines | 1.9.0 |
| JUnit (tests) | 4.13.2 |
| Android Gradle Plugin | 8.5.2 |
| Gradle | 8.7 |
| compileSdk / targetSdk | 34 (Android 14) |
| minSdk | 26 (Android 8.0+) |
| applicationId / versión | `com.sipe.app` · 1.0.0 |

## Base de datos: Room, la persistencia nativa de Android

Se eligió **Room** (librería oficial de Jetpack sobre SQLite) porque es la vía nativa recomendada: verifica el SQL en compilación mediante KSP, expone los datos como `Flow` reactivo para Compose, soporta migraciones versionadas y no requiere servidor ni internet (offline-first, ideal para uso estudiantil).

**Esquema (`SipeDatabase`, versión 2):**

| Tabla | Campos clave |
|---|---|
| `users` | id, name, email (único), passwordHash (PBKDF2 + salt), university, career |
| `courses` | id, name, professor, classroom, dayOfWeek, startTime, endTime, credits, colorHex |
| `tasks` | id, title, description, courseName, dueDate (AAAA-MM-DD), priority, done |
| `grades` | id, courseName, label, value (0–5), weight (0–1) |
| `goals` | id, title, target, progress |

**DataStore (`sipe_prefs`):** sesión (nombre, correo, logged), racha (current, best, points, freezes, lastDate) y tema oscuro.

## Estructura del proyecto

```
app/src/main/java/com/sipe/app/
  MainActivity.kt · SipeApplication.kt
  data/       Entities.kt, SipeDatabase.kt, Security.kt, SessionManager.kt, SipeRepository.kt
  domain/     StreakCalculator.kt, Levels.kt
  ui/         AppViewModels.kt, theme/, nav/AppNav.kt
  ui/screens/ auth/, home/, calendar/, tasks/, classes/, plan/, settings/, components/
app/src/main/res/
  drawable-nodpi/ic_logo_mark.png   (logo en la app)
  mipmap-*/ic_launcher(.png)        (icono por densidad + adaptativo)
  values/  strings, colors, themes
app/src/test/  StreakCalculatorTest.kt
docs/  brand/, play/, wireframes_papel/, ESTADO.md, PUBLICACION.md, WIREFRAMES.md, ...
```

## Cómo ejecutar

1. Clona el repositorio:
   ```bash
   git clone https://github.com/juann11/App_Nativa_SIPE.git
   ```
2. Ábrelo en Android Studio y sincroniza Gradle.
3. Conecta un dispositivo Android con depuración USB y pulsa Run.
4. Entra con la cuenta de demostración o crea la tuya con **Crear cuenta**.

**Cuenta demo:** `admin@sipe.edu` / `Admin123`

Tests: `./gradlew :app:testDebugUnitTest`.

## Publicación

La ficha de tienda (nombre, descripciones, `icono_512.png`), la guía de firma del `.aab` y el proceso de Play Console están en [`docs/PUBLICACION.md`](docs/PUBLICACION.md) y [`docs/play/`](docs/play/).

## Diseño

Logo original en [`docs/brand/`](docs/brand/).

**Wireframes digitales (Figma):** [https://www.figma.com/design/WxLn39PaUl9Ev3NVi8s5r6/SIPE?node-id=0-1&p=f&t=srasZ0yfTNVzsDPD-0](https://www.figma.com/design/WxLn39PaUl9Ev3NVi8s5r6/SIPE?node-id=0-1&p=f&t=srasZ0yfTNVzsDPD-0)

**Wireframes en papel:** fotos en [`docs/wireframes_papel/`](docs/wireframes_papel/).


## Equipo

- Juan José Ospina Arroyave — Ingeniería de Sistemas e Informática.
