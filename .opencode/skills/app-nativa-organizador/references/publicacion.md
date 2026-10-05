# Firma, .aab y Google Play

Verificar en la web los datos que cambian (API level mínimo exigido, costo de la cuenta de desarrollador, requisito de pruebas cerradas para cuentas nuevas, formularios obligatorios) antes de afirmarlos.

## Preparar el build
1. `applicationId` definitivo (no se puede cambiar tras publicar), `versionCode` entero creciente, `versionName`.
2. Nombre visible, ícono adaptativo (Image Asset Studio), splash.
3. Activar minify/R8 y shrinkResources en release; probar el APK release en el dispositivo físico antes de subir.
4. Quitar logs y claves de prueba; revisar permisos declarados (solo los necesarios, por ejemplo POST_NOTIFICATIONS).

## Firma
- Generar keystore: Build > Generate Signed Bundle > Create new (o `keytool`). Guardar el .jks y las contraseñas fuera del repositorio y con copia de respaldo; perderlo complica actualizar la app.
- Inscribirse en Play App Signing al subir el primer .aab.
- Leer credenciales desde `keystore.properties` ignorado por git, no escribirlas en build.gradle.

## Generar el .aab
Android Studio: Build > Generate Signed Bundle / APK > Android App Bundle > release. O `./gradlew bundleRelease`. Salida: `app/release/*.aab`.

## Play Console
1. Cuenta de desarrollador (pago único, verificación de identidad).
2. Crear app: nombre, idioma, app o juego, gratis o de pago.
3. Ficha de tienda: descripción corta (80 car.) y completa (4000 car.), ícono 512x512, gráfico de funciones 1024x500, capturas de teléfono (mínimo 2).
4. Contenido de la app: política de privacidad (URL pública, obligatoria si hay datos de usuario), seguridad de los datos, clasificación de contenido, público objetivo, anuncios.
5. Subir el .aab a una pista (interna, cerrada o producción) y enviar a revisión. Las cuentas personales nuevas pueden requerir pruebas cerradas con un número mínimo de testers durante un período antes de producción: confirmar el requisito vigente y planificarlo desde el inicio, porque afecta el cronograma de la entrega.
6. Compartir con el docente el enlace de la ficha o el de pruebas si la revisión aún no termina.

## Errores frecuentes
- Subir un .apk en lugar de .aab.
- versionCode repetido.
- Política de privacidad faltante.
- targetSdk por debajo de lo exigido.
- Keystore perdido o subido al repositorio.
