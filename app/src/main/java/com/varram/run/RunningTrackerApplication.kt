package com.varram.run
import org.osmdroid.config.Configuration
import android.app.Application
import com.varram.run.feature.home.presentation.LocationRepository

class RunningTrackerApplication : Application() {

    lateinit var locationRepository: LocationRepository
        private set

    override fun onCreate() {
        super.onCreate()

        Configuration.getInstance().userAgentValue = packageName

        locationRepository = LocationRepository()
    }
}