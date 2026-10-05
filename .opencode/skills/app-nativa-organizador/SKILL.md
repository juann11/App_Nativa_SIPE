---
name: app-nativa-organizador
description: Guía completa para construir, probar, firmar y publicar en Google Play la app nativa Android (Kotlin, Android Studio) de la Entrega 3 - un organizador estudiantil tipo iStudiez Pro (calendario de clases, tareas, notas, planificación del semestre, sistema de rachas). Úsala siempre que el usuario mencione la Entrega 3, APP Nativa, Android Studio, Kotlin, Jetpack Compose, wireframes/Figma de la app, Room, rachas, calendario o tareas de la app, firmar la app, generar el .aab, Play Console, publicar en la tienda o armar la presentación de la entrega, aunque no diga "skill" ni nombre el proyecto.
---

# App nativa organizador estudiantil (Entrega 3)

Proyecto académico en equipo: app Android nativa en Kotlin, publicada en Google Play. Concepto: organizador estilo iStudiez Pro que una universidad podría adoptar como app oficial. Funciones núcleo: calendario de clases, tareas, notas, planificación del semestre y sistema de rachas.

## Antes de escribir código

1. Buscar el README del repositorio (en /mnt/user-data/uploads o pedirlo pegado). Es la fuente de verdad sobre la estructura ya propuesta por el equipo. Si contradice esta skill, gana el README, salvo que viole un requisito de la entrega.
2. Si el README no está disponible, hacer UNA pregunta con lo mínimo que bloquea (Compose o XML; backend o solo local) y proceder con los defaults de abajo.
3. Leer solo la referencia que la tarea necesita:
   - Arquitectura, modelo de datos, rachas, código: `references/arquitectura.md`
   - Firma, .aab, Play Console, ficha de tienda: `references/publicacion.md`
   - Wireframes, Figma, checklist de rúbrica, presentación: `references/entrega.md`

## Requisitos de la entrega (no negociables)

- Wireframe manual en papel, digitalizado en Figma, subido al repositorio.
- Pantallas mínimas: login, registro, menú, listado, detalle, formulario, configuración, créditos (información del estudiante).
- Implementar las funcionalidades de la app elegida.
- Probar en dispositivo físico (el target de instalación).
- Preparar lanzamiento: nombre, recursos gráficos, descripción.
- Firmar y generar el .aab; publicar en Google Play.
- Presentación final: arquitectura, vistas, funcionalidades principales y publicación en la tienda.
- El trabajo en equipo pesa en la nota: mantener commits repartidos y ramas por integrante.

## Defaults técnicos (si el README no dice otra cosa)

- Kotlin + Jetpack Compose + Material 3, minSdk 26, targetSdk el vigente que exija Play (verificar en la documentación actual).
- MVVM con capas ui / domain / data, StateFlow, Navigation Compose.
- Persistencia local con Room; preferencias con DataStore; recordatorios con WorkManager + notificaciones.
- Inyección de dependencias con Hilt (o manual si el equipo no lo domina).
- Login y registro: decidir una sola vía y decirla al usuario: (a) Firebase Auth, (b) cuentas locales en Room con contraseña hasheada. Para una entrega académica con plazo corto, (b) o Firebase Auth con email/contraseña son las opciones viables; no inventar backend propio.
- Rachas: lógica pura en `domain`, testeable sin Android (ver arquitectura.md).

## Cómo trabajar con el usuario

- Entregar código completo y funcional por archivo, con paquete e imports correctos; no fragmentar sin razón. Comentarios solo donde aporten.
- Avanzar por hitos y cuando el usuario confirme uno, pasar al siguiente sin pedir confirmación. Orden sugerido: 1) estructura del proyecto y navegación con pantallas vacías, 2) Room + modelos, 3) login/registro, 4) tareas (listado, detalle, formulario), 5) calendario y horario de clases, 6) notas y planificación del semestre, 7) rachas y notificaciones, 8) configuración y créditos, 9) pruebas en dispositivo físico, 10) firma, .aab y Play Console, 11) presentación.
- Respuestas cortas para preguntas cortas. Sin advertencias no pedidas.
- Nunca afirmar que algo compila o funciona sin haberlo verificado; si no hay entorno Android disponible, decir que el código no fue compilado y listar qué revisar al sincronizar Gradle.
- Datos volátiles (requisitos de Play Console, API level exigido, costo de la cuenta, período de pruebas cerradas): buscar en la web y citar antes de afirmarlos.

## Presentación

Diapositivas simples, legibles en TV, lenguaje llano, una idea por diapositiva. Estructura en `references/entrega.md`. Si el usuario pide el archivo, leer primero el SKILL.md de pptx.
