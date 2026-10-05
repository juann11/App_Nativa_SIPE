# SIPE (Sistema Integrado de Planificación Estudiantil)

SIPE es la propuesta de proyecto del estudiante para la Entrega 3 (tema libre permitido: "El estudiante puede proponer un proyecto").
Equivale a Checklist de tareas + Finanzas/metas + Calendario académico, tipo iStudiez Pro pero como app oficial de la universidad, con sistema de rachas para incentivar el uso diario. Funciona 100% offline con Room (login simulado en local, como pedía la Entrega 1).

## 🚀 Características Principales (Módulos exigidos + extras)

* **Autenticación (Login/Registro):** `LoginScreen` / `RegisterScreen` con credenciales institucionales (Room local en Fase 1, Firebase Auth en Fase B2B).
* **Dashboard (Menú Principal):** `DashboardScreen` con racha, puntos, próximas tareas y materias.
* **Módulo de Clases (Listado y Detalle):** `ClassesScreen` + `ClassDetailScreen` (salón, profesor, créditos, horario).
* **Gestión Académica (Formulario):** `TaskFormScreen` para tareas/recordatorios + `GradesPlanScreen` para notas y metas del semestre.
* **Preferencias (Configuración):** `SettingsScreen` (tema oscuro, notificaciones, cerrar sesión).
* **Acerca de (Créditos):** `AboutScreen` con versión y equipo.
* **Extra diferenciador:** Sistema de rachas 🔥 (`SessionManager.registerDailyActivity()`): +10 pts/día, +2 si re-entra, +50 bonus cada 7 días, niveles Novato → Leyenda SIPE.

## 🛠 Herramientas y Tecnologías

* **Nativo:** Android Studio + Kotlin + Jetpack Compose (Material3) + Navigation Compose.
* **Arquitectura:** MVVM. `MainActivity` → `AppNav` → `ViewModels` → `SipeRepository` → `Room` + `DataStore`.
* **Persistencia Fase 1 (entrega):** Room (`User, Course, TaskItem, Grade, SemesterGoal`) + DataStore (sesión + racha + tema).
* **Fase 2 B2B (universidad real):** Firebase Auth + Firestore + FCM. Dependencias listas pero comentadas en `app/build.gradle.kts`. Ver `docs/PUBLICACION.md`.
* **Diseño UI/UX:** Figma. Ver `docs/WIREFRAMES.md`.
* **Distribución:** App Bundle (.aab) firmado para Google Play Console.

## 📁 Estructura

```
app/src/main/java/com/sipe/app/
  MainActivity.kt
  SipeApplication.kt
  data/ Entities.kt, SipeDatabase.kt, SessionManager.kt, SipeRepository.kt
  ui/ AppViewModels.kt, theme/Theme.kt, nav/AppNav.kt, screens/AuthScreens.kt, screens/MainScreens.kt
docs/
  WIREFRAMES.md
  PUBLICACION.md
  PRESENTACION.md
  CHECKLIST_ENTREGA3.md
  play/ (ficha de tienda: descripciones + checklist recursos)
```

## ✅ Cobertura Entrega 3 (según https://xacarana.com/cursos/aplicaciones_moviles/#/6/2)

| Pedido | Estado SIPE |
|---|---|
| Tema libre propuesto | OK: organizador universitario (checklist + metas + horario) |
| Wireframe papel (8 secciones) | Pendiente foto: `docs/wireframes_papel/` ver `docs/WIREFRAMES.md` |
| Wireframe Figma digital | Pendiente link en sección Diseño |
| login, registro, menú, listado, detalle, formulario, configuración, créditos | OK en `ui/nav/AppNav.kt` + `ui/screens/` |
| Montaje Android Studio + Kotlin | OK: Compose (ruta oficial de referentes) |
| Funcionalidades | OK: CRUD tareas/notas/metas + rachas |
| Prueba en físico (target) | Ver `docs/PUBLICACION.md` |
| Nombre, gráficos, descripción + .aab firmado + Play | Ver `docs/play/` + `docs/PUBLICACION.md` |
| Presentación arquitectura/vistas/funciones/publicación | Ver `docs/PRESENTACION.md` |

## ⚙️ Requisitos e Instalación (Para el evaluador)

1. Clonar:
   ```bash
   git clone https://github.com/juann11/App_Nativa_SIPE.git
   ```
2. Abrir la carpeta en Android Studio (Hedgehog o superior).
3. Sincronizar Gradle (usa Gradle 8.7, AGP 8.5.2, Kotlin 2.0.20).
4. Conectar dispositivo físico Android con depuración USB activada.
5. Run (Shift + F10). Usuario de prueba: regístrate con tu correo institucional, la BD trae 5 materias, 3 tareas, notas y metas de ejemplo.

## 📄 Diseño (Wireframes)

* Papel: fotos en `/docs/wireframes_papel/` (subir escaneos).
* Digital: [Enlace a Figma aquí] — estructura propuesta en `docs/WIREFRAMES.md`.

## 👥 Créditos / Desarrolladores

Juan José Ospina Arroyave - Ingeniería Sistemas e Informática.
