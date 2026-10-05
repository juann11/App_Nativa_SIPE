# Publicación SIPE — pruebas, firma y Play Console

## 1. Probar en dispositivo físico (target)
1. Activar Opciones de desarrollador + Depuración USB en el celular.
2. Conectar por USB, aceptar huella.
3. En Android Studio: seleccionar `app` + tu dispositivo + Run.
4. Checklist: login, registro, crear tarea, completar tarea (verifica que la racha sube), ver detalle de clase, agregar nota, cambiar tema, cerrar sesión.

## 2. Preparar lanzamiento
- Nombre: `SIPE - Planificación Estudiantil`
- applicationId: `com.sipe.app` (app/build.gradle.kts)
- versionCode 1, versionName 1.0.0
- Icono: generar en Resource Manager (Foreground + Background) o usar Image Asset con el logo SIPE.
- Descripción corta (80 car.): "Horario, tareas, notas y rachas de estudio de tu universidad."
- Descripción larga: usar `app/src/main/res/values/strings.xml` (`app_desc`) + mencionar rachas y planificación.

## 3. Firmar y generar .aab
1. Build > Generate Signed App Bundle / APK > Android App Bundle.
2. Create new keystore (`sipe-release.jks`), alias `sipe`, guardar passwords en lugar seguro (NO subir a git).
3. Build Type: release. Genera `app/release/app-release.aab`.
4. Verificar: `jarsigner -verify app-release.aab` o subir directo a Play Console > Prueba interna.

## 4. Google Play Console (según Taller #2 + Entrega 3)

0. Solicitar acceso a la tienda: enviar correo por equipo desde Gmail a `lasmovies@gmail.com`, esperar invitación y aceptarla.
1. Crear app, categoría Educación.
2. Completar ficha (ver `docs/play/`): icono PNG 512x512, feature graphic 1024x500, mínimo 4 capturas a 1080px, descripción corta + completa.
3. Subir .aab a Prueba interna → agregar testers → luego Producción.
4. Contenido: cuestionario de clasificación, público objetivo, política de privacidad (requerida aunque sea local, genera una en https://terminosycondicionesdeusoejemplo.com/), declarar si usa Firebase (Fase 2).

## 5. Fase B2B Firebase (post-entrega)
1. Firebase Console > crear proyecto `sipe-universidad` > agregar app Android `com.sipe.app`.
2. Descargar `google-services.json` a `app/`.
3. Descomentar en `build.gradle.kts` raíz y `app/build.gradle.kts` las líneas Firebase.
4. Migrar `SipeRepository` a Firestore por `universityId` (multi-tenant) y Auth institucional.
