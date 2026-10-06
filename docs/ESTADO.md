# Sondeo SIPE — dónde estamos

## ✅ Lo que tenemos (funciona hoy)

- **Cuenta:** registro y login locales con PBKDF2 + salt, sesión persistida, validaciones en español.
- **Hoy:** agenda del día, resumen (pendientes, materias, promedio) y tarjeta de racha con mascota de nivel.
- **Calendario:** mes en español, modos Horario/Tareas, puntos en días con eventos, detalle del día, estados vacíos amables.
- **Tareas:** tabs Actuales/Completadas, orden por fecha/materia/prioridad, grupos por día, badge con conteo, formulario con desplegable de materias y mini-calendario de fecha.
- **Horario:** listado con color por materia y detalle con pendientes + entregadas.
- **Plan:** estado del semestre (promedio, aprobando, vencidas), resumen por materia, notas con peso %.
- **Rachas y niveles:** lógica pura testeada (8 tests), freezes, bonus semanal, 5 niveles con mascota (🐣→🐉) visibles en Hoy y Ajustes.
- **Ajustes y Créditos:** cuenta, tema oscuro, niveles, navegación con vuelta atrás.
- **Técnico:** 100 % offline (Room + DataStore), icono adaptativo, release con R8, `docs/play/` lista.

## ❌ Lo que falta (para cerrar la Entrega 3)

1. Fotos de wireframes en papel → `docs/wireframes_papel/`.
2. Enlace del Figma en README.
3. Icono 512×512, feature graphic 1024×500 y 4 capturas a 1080 px.
4. Probar en el físico target y registrar evidencias.
5. Generar el `.aab` firmado (keystore fuera del repo).
6. Pedir acceso a Play (`lasmovies@gmail.com`) y subir a Prueba interna.
7. Política de privacidad pública.
8. Presentación (guion en `docs/PRESENTACION.md`).

## 💡 Posibles mejoras (post-entrega)

- Notificaciones diarias con WorkManager (avisar si la racha está en riesgo).
- Firebase multi-universidad: Auth institucional + Firestore + FCM.
- Widget de "Hoy" y accesos directos a tarea nueva.
- Exportar/compartir horario y reporte de notas (PDF).
- Onboarding inicial y edición/eliminación de materias desde la app.
- Estadísticas de estudio semanal.
