package com.varram.run.service


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.varram.run.RunningTrackerApplication
import com.varram.run.core.location.LocationTracker
import com.varram.run.data.model.LocationData
import com.varram.run.feature.home.presentation.LocationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class LocationTrackingService : Service() {

    private val serviceScope =
        CoroutineScope(
            SupervisorJob() + Dispatchers.Default
        )
    private lateinit var locationRepository: LocationRepository
    private lateinit var locationTracker: LocationTracker

    private var trackingJob: Job? = null

    override fun onCreate() {
        super.onCreate()

        locationTracker =
            LocationTracker(applicationContext)
        locationRepository =
            (application as RunningTrackerApplication)
                .locationRepository

        createNotificationChannel()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        when (intent?.action) {

            ACTION_START_TRACKING -> {
                startTracking()
            }

            ACTION_STOP_TRACKING -> {
                stopTracking()
            }
        }

        return START_STICKY
    }

    private fun startTracking() {

        // Prevent duplicate collectors
        if (trackingJob?.isActive == true) {
            return
        }

        startForeground(
            NOTIFICATION_ID,
            createNotification()
        )

        trackingJob = serviceScope.launch {

            try {

                locationTracker
                    .locationUpdates()
                    .collect { location ->

                        handleLocation(location)
                    }

            } catch (e: CancellationException) {

                // Expected when tracking stops

                throw e

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "Location tracking failed",
                    e
                )
            }
        }
    }
    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Running Tracker",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows when running location tracking is active"
            }

            val notificationManager =
                getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun createNotification(): Notification {

        return NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )
            .setContentTitle("Run in progress")
            .setContentText("Tracking your location")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    private fun handleLocation(
        location: LocationData
    ) {
        locationRepository.updateLocation(location)

        Log.d(
            TAG,
            """
            Location Update
            lat=${location.latitude}
            lon=${location.longitude}
            accuracy=${location.accuracy}
            speed=${location.speed}
            bearing=${location.bearing}
            altitude=${location.altitude}
            timestamp=${location.timestamp}
            """.trimIndent()
        )

        // Later:
        // repository.processLocation(location)
    }

    private fun stopTracking() {

        trackingJob?.cancel()
        trackingJob = null

        stopForeground(
            STOP_FOREGROUND_REMOVE
        )

        stopSelf()
    }

    override fun onDestroy() {

        trackingJob?.cancel()

        serviceScope.cancel()

        super.onDestroy()
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = null

    companion object {

        const val ACTION_START_TRACKING =
            "com.yourpackage.runningtracker.START_TRACKING"

        const val ACTION_STOP_TRACKING =
            "com.yourpackage.runningtracker.STOP_TRACKING"

        private const val TAG =
            "LocationTrackingService"
        private const val CHANNEL_ID =
            "running_tracker_channel"
        private const val NOTIFICATION_ID =
            1001
    }
}