package com.sipe.app.data

class SipeRepository(
    private val db: SipeDatabase,
    private val session: SessionManager
) {
    fun courses() = db.courseDao().observeAll()
    fun tasks() = db.taskDao().observeAll()
    fun pendingTasks() = db.taskDao().observePending()
    fun grades() = db.gradeDao().observeAll()
    fun goals() = db.goalDao().observeAll()

    suspend fun getCourse(id: Long) = db.courseDao().getById(id)
    suspend fun saveTask(t: TaskItem) = db.taskDao().upsert(t)
    suspend fun deleteTask(t: TaskItem) = db.taskDao().delete(t)
    suspend fun toggleTask(id: Long, done: Boolean) {
        db.taskDao().setDone(id, done)
        if (done) session.registerDailyActivity()
    }
    suspend fun saveGrade(g: Grade) = db.gradeDao().upsert(g)
    suspend fun deleteGrade(g: Grade) = db.gradeDao().delete(g)
    suspend fun saveGoal(g: SemesterGoal) = db.goalDao().upsert(g)
    suspend fun deleteGoal(g: SemesterGoal) = db.goalDao().delete(g)
    suspend fun saveCourse(c: Course) = db.courseDao().insert(c)

    suspend fun login(email: String, password: String): Result<String> {
        val clean = email.trim().lowercase()
        if (clean.isBlank() || password.isEmpty()) {
            return Result.failure(IllegalArgumentException("Ingresa correo y contraseña"))
        }
        val user = db.userDao().findByEmail(clean)
            ?: return Result.failure(IllegalArgumentException("Usuario no registrado"))
        val ok = if (user.salt.isBlank()) {
            // Cuenta anterior a PBKDF2: verificar y migrar al entrar.
            if (!Security.verifyLegacy(password, user.passwordHash)) {
                return Result.failure(IllegalArgumentException("Contraseña incorrecta"))
            }
            val salt = Security.generateSalt()
            db.userDao().update(user.copy(salt = salt, passwordHash = Security.hash(password, salt)))
            true
        } else {
            Security.verify(password, user.salt, user.passwordHash)
        }
        if (!ok) return Result.failure(IllegalArgumentException("Contraseña incorrecta"))
        session.login(user.name, user.email)
        return Result.success(user.name)
    }

    suspend fun register(name: String, email: String, password: String): Result<String> {
        val cleanName = name.trim()
        val clean = email.trim().lowercase()
        if (cleanName.isBlank()) {
            return Result.failure(IllegalArgumentException("Ingresa tu nombre completo"))
        }
        if (!EMAIL_RE.matches(clean)) {
            return Result.failure(IllegalArgumentException("Usa un correo válido"))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("La contraseña debe tener mínimo 6 caracteres"))
        }
        if (db.userDao().findByEmail(clean) != null) {
            return Result.failure(IllegalArgumentException("Ese correo institucional ya existe"))
        }
        val salt = Security.generateSalt()
        db.userDao().insert(
            User(
                name = cleanName,
                email = clean,
                passwordHash = Security.hash(password, salt),
                salt = salt
            )
        )
        session.login(cleanName, clean)
        return Result.success(cleanName)
    }

    /**
     * Seeds que se fusionan: agregan lo que falte sin borrar ni duplicar
     * lo que el usuario ya tiene. Así las tareas de ejemplo aparecen
     * aunque la BD ya tenga datos de versiones anteriores.
     */
    suspend fun seedIfEmpty() {
        val existingCourses = db.courseDao().names().toSet()
        listOf(
            Course(name = "Cálculo Diferencial", professor = "Dra. M. Ruiz", classroom = "Bloque B-204", dayOfWeek = "LUN", startTime = "08:00", endTime = "10:00", credits = 4, colorHex = "#1B5E20"),
            Course(name = "Programación Móvil", professor = "Ing. J. Ospina", classroom = "Lab 3", dayOfWeek = "MAR", startTime = "10:00", endTime = "12:00", credits = 3, colorHex = "#1565C0"),
            Course(name = "Física I", professor = "Dr. A. Torres", classroom = "Bloque A-101", dayOfWeek = "MIE", startTime = "14:00", endTime = "16:00", credits = 4, colorHex = "#6A1B9A"),
            Course(name = "Inglés Técnico", professor = "Lic. S. Pérez", classroom = "Virtual", dayOfWeek = "JUE", startTime = "08:00", endTime = "09:30", credits = 2, colorHex = "#EF6C00"),
            Course(name = "Bases de Datos", professor = "Ing. L. Gómez", classroom = "Lab 1", dayOfWeek = "VIE", startTime = "10:00", endTime = "12:00", credits = 3, colorHex = "#00695C"),
            Course(name = "Estructuras de Datos", professor = "Ing. R. Castaño", classroom = "Lab 2", dayOfWeek = "MAR", startTime = "14:00", endTime = "16:00", credits = 3, colorHex = "#AD1457"),
            Course(name = "Ética Profesional", professor = "Dra. P. León", classroom = "Bloque C-301", dayOfWeek = "LUN", startTime = "10:30", endTime = "12:00", credits = 2, colorHex = "#00838F")
        ).filter { it.name !in existingCourses }.forEach { db.courseDao().insert(it) }

        val existingTitles = db.taskDao().titles().toSet()
        listOf(
            TaskItem(title = "Taller de Kotlin - Compose", courseName = "Programación Móvil", dueDate = "2026-10-05", priority = "ALTA", description = "Terminar dashboard + rachas"),
            TaskItem(title = "Parcial Cálculo", courseName = "Cálculo Diferencial", dueDate = "2026-10-08", priority = "ALTA", description = "Capítulos 3 y 4: derivadas"),
            TaskItem(title = "Informe laboratorio Física", courseName = "Física I", dueDate = "2026-10-10", priority = "MEDIA", description = "Movimiento rectilíneo + gráficas"),
            TaskItem(title = "Quiz verbos técnicos", courseName = "Inglés Técnico", dueDate = "2026-10-03", priority = "MEDIA"),
            TaskItem(title = "Modelo ER biblioteca", courseName = "Bases de Datos", dueDate = "2026-10-06", priority = "ALTA", description = "Entregar diagrama + script SQL"),
            TaskItem(title = "Leer guía de laboratorio", courseName = "Física I", dueDate = "2026-10-02", priority = "BAJA", done = true),
            TaskItem(title = "Repaso semana 8", courseName = "Cálculo Diferencial", dueDate = "2026-10-12", priority = "BAJA"),
            TaskItem(title = "Avance proyecto final", courseName = "Programación Móvil", dueDate = "2026-10-15", priority = "ALTA", description = "Publicar .aab en Play Console"),
            TaskItem(title = "Quiz listas enlazadas", courseName = "Estructuras de Datos", dueDate = "2026-10-07", priority = "MEDIA"),
            TaskItem(title = "Ensayo dilemas IA", courseName = "Ética Profesional", dueDate = "2026-10-09", priority = "BAJA", description = "2 páginas, normas APA")
        ).filter { it.title !in existingTitles }.forEach { db.taskDao().upsert(it) }
        if (db.gradeDao().count() == 0) {
            db.gradeDao().upsert(Grade(courseName = "Programación Móvil", label = "Parcial 1", value = 4.5, weight = 0.3))
            db.gradeDao().upsert(Grade(courseName = "Programación Móvil", label = "Quiz Compose", value = 5.0, weight = 0.2))
            db.gradeDao().upsert(Grade(courseName = "Cálculo Diferencial", label = "Parcial 1", value = 3.8, weight = 0.35))
        }
        if (db.goalDao().count() == 0) {
            db.goalDao().upsert(SemesterGoal(title = "Promedio ≥ 4.0", target = 100, progress = 65))
            db.goalDao().upsert(SemesterGoal(title = "0 tareas vencidas", target = 100, progress = 40))
            db.goalDao().upsert(SemesterGoal(title = "Racha de 30 días", target = 30, progress = 5))
        }
    }

    /** Promedio ponderado por materia para la vista de Notas. */
    fun averageByCourse(grades: List<Grade>): Map<String, Double> {
        return grades.groupBy { it.courseName }.mapValues { (_, list) ->
            val w = list.sumOf { it.weight }.takeIf { it > 0 } ?: 1.0
            list.sumOf { it.value * it.weight } / w
        }
    }

    companion object {
        val EMAIL_RE = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
