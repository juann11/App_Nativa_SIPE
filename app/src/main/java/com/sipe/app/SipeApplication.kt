package com.sipe.app

import android.app.Application
import androidx.room.Room
import com.sipe.app.data.SessionManager
import com.sipe.app.data.SipeDatabase
import com.sipe.app.data.SipeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SipeApplication : Application() {
    val database by lazy {
        Room.databaseBuilder(this, SipeDatabase::class.java, "sipe.db")
            .fallbackToDestructiveMigration()
            .build()
    }
    val session by lazy { SessionManager(this) }
    val repository by lazy { SipeRepository(database, session) }

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appScope.launch { repository.seedIfEmpty() }
    }
}
