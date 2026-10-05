package com.sipe.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// ---------- Entidades ----------

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val passwordHash: String,
    val salt: String = "",
    val university: String = "Universidad SIPE",
    val career: String = "Ingeniería de Sistemas"
)

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val professor: String,
    val classroom: String,
    val dayOfWeek: String, // LUN, MAR, MIE, JUE, VIE, SAB
    val startTime: String, // "08:00"
    val endTime: String,   // "10:00"
    val credits: Int = 3,
    val colorHex: String = "#1B5E20"
)

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val courseId: Long? = null,
    val courseName: String = "General",
    val dueDate: String = "", // "2026-10-05"
    val priority: String = "MEDIA", // ALTA, MEDIA, BAJA
    val done: Boolean = false
)

@Entity(tableName = "grades")
data class Grade(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val courseId: Long = 0,
    val courseName: String,
    val label: String, // "Parcial 1", "Quiz", "Proyecto"
    val value: Double, // 0.0 - 5.0
    val weight: Double = 0.25 // 0-1
)

@Entity(tableName = "goals")
data class SemesterGoal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val target: Int = 100,
    val progress: Int = 0
)

// ---------- DAOs ----------

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): User?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: User): Long

    @Update
    suspend fun update(user: User)
}

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY dayOfWeek, startTime")
    fun observeAll(): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Course?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Course>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Course): Long

    @Delete
    suspend fun delete(item: Course)

    @Query("SELECT COUNT(*) FROM courses")
    suspend fun count(): Int

    @Query("SELECT name FROM courses")
    suspend fun names(): List<String>
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY done ASC, dueDate ASC")
    fun observeAll(): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks WHERE done = 0 ORDER BY dueDate ASC")
    fun observePending(): Flow<List<TaskItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: TaskItem): Long

    @Delete
    suspend fun delete(item: TaskItem)

    @Query("UPDATE tasks SET done = :done WHERE id = :id")
    suspend fun setDone(id: Long, done: Boolean)

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun count(): Int

    @Query("SELECT title FROM tasks")
    suspend fun titles(): List<String>
}

@Dao
interface GradeDao {
    @Query("SELECT * FROM grades ORDER BY courseName, id")
    fun observeAll(): Flow<List<Grade>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: Grade): Long

    @Delete
    suspend fun delete(item: Grade)

    @Query("SELECT COUNT(*) FROM grades")
    suspend fun count(): Int
}

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY id")
    fun observeAll(): Flow<List<SemesterGoal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: SemesterGoal): Long

    @Delete
    suspend fun delete(item: SemesterGoal)

    @Query("SELECT COUNT(*) FROM goals")
    suspend fun count(): Int
}
