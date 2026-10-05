package com.sipe.app.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [User::class, Course::class, TaskItem::class, Grade::class, SemesterGoal::class],
    version = 2,
    exportSchema = false
)
abstract class SipeDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun courseDao(): CourseDao
    abstract fun taskDao(): TaskDao
    abstract fun gradeDao(): GradeDao
    abstract fun goalDao(): GoalDao
}
