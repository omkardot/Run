package com.varram.run
import org.osmdroid.config.Configuration
import android.app.Application
import androidx.room.Room
import com.varram.run.data.local.database.RunningDatabase
import com.varram.run.data.repository.RunningRepository
import com.varram.run.feature.tracking.presentation.LocationRepository

class RunningTrackerApplication : Application() {

    lateinit var database: RunningDatabase
        private set

    lateinit var runningRepository: RunningRepository
        private set

    override fun onCreate() {
        super.onCreate()

        Configuration.getInstance().userAgentValue =
            packageName

        database = Room.databaseBuilder(
            applicationContext,
            RunningDatabase::class.java,
            "running_tracker.db"
        ).build()

        runningRepository = RunningRepository(
            runDao = database.runDao(),
            locationPointDao = database.locationPointDao()
        )
    }
}