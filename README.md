# SIPE — Sistema Integrado de Planificación Estudiantil

SIPE es la app oficial de tu universidad para organizar la vida académica: horario de clases, tareas y recordatorios, notas con promedio ponderado, resumen del semestre y un sistema de rachas que te motiva a estudiar todos los días. Diseñada para que la institución la adopte como su app estudiantil. Funciona 100 % sin internet: tus datos viven en el dispositivo.

## Funcionalidades

- **Hoy:** agenda del día (clases + entregas), resumen de pendientes, materias y promedio, y tu racha.
- **Calendario:** vista mensual en español con modos Horario y Tareas, puntos en los días con eventos y detalle del día elegido.
- **Tareas:** pestañas Actuales/Completadas, orden por fecha, materia o prioridad, y agrupación por día de entrega.
- **Horario:** listado de materias con profesor, salón, horario y créditos, más detalle con sus entregas pendientes y realizadas.
- **Planificador:** estado del semestre (promedio, materias aprobando, tareas vencidas), resumen por materia y notas con peso porcentual.
- **Rachas y niveles:** cada día activo suma puntos. Niveles con mascota: 🐣 Novato → 🐰 En racha → 🦊 Avanzado → 🦉 Maestro → 🐉 Leyenda SIPE. Incluye congelamientos que te salvan un día perdido y bonus cada 7 días.
- **Ajustes y créditos:** cuenta, tema oscuro, tu nivel y la información del equipo.

## Tecnología

- **App nativa:** Kotlin + Jetpack Compose + Material 3 (minSdk 26).
- **Arquitectura:** MVVM con capas `ui / domain / data`, Navigation Compose y StateFlow.
- **Datos locales:** Room (usuarios, materias, tareas, notas) y DataStore (sesión, racha, tema). Sin backend: ideal para usar offline.
- **Calidad:** lógica de rachas como código puro con tests unitarios (`StreakCalculatorTest`).

```
app/src/main/java/com/sipe/app/
  MainActivity.kt · SipeApplication.kt
  data/      Room + DataStore + seguridad PBKDF2
  domain/    rachas, niveles y reglas de negocio
  ui/        navegación, pantallas y tema
```

## Cómo ejecutar

1. Clona el repositorio:
   ```bash
   git clone https://github.com/juann11/App_Nativa_SIPE.git
   ```
2. Ábrelo en Android Studio y sincroniza Gradle.
3. Conecta un dispositivo Android con depuración USB y pulsa Run.
4. Crea tu cuenta con el botón **Crear cuenta** e inicia sesión. La app incluye materias, tareas y notas de ejemplo.

Para correr los tests: `./gradlew :app:testDebugUnitTest`.

## Publicación

La ficha de tienda (nombre, descripciones, icono y capturas), la guía de firma del `.aab` y el proceso de Play Console están en [`docs/PUBLICACION.md`](docs/PUBLICACION.md) y [`docs/play/`](docs/play/).

## Diseño

Wireframes en papel y archivo interactivo de Figma: ver [`docs/WIREFRAMES.md`](docs/WIREFRAMES.md).

## Equipo

- Juan José Ospina Arroyave — Ingeniería de Sistemas e Informática.
