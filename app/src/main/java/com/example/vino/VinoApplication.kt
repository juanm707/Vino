package com.example.vino

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.CoilUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import com.example.vino.database.VinoDatabase
import com.example.vino.network.VinoApi
import com.example.vino.network.VinoApiService
import com.example.vino.repository.VinoRepository
import okhttp3.OkHttpClient

class VinoApplication : Application() {
    // No need to cancel this scope as it'll be torn down with the process
    private val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { VinoDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { VinoRepository(database.todoDao(), database.blockDao(), database.coordinateDao(), VinoApi.retrofitService) }
}
