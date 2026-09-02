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
import com.varram.run.data.domain.RunningTrackerEngine
import com.varram.run.data.domain.tracker.LocationFilter
import com.varram.run.data.local.database.RunningDatabase
import com.varram.run.data.model.LocationData
import com.varram.run.data.repository.RunningRepository
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

    private lateinit var locationTracker: LocationTracker
    private lateinit var runningRepository: RunningRepository
    private lateinit var trackerEngine: RunningTrackerEngine

    private val locationFilter = LocationFilter()
    private var trackingJob: Job? = null

    override fun onCreate() {
        super.onCreate()

        locationTracker =
            LocationTracker(applicationContext)

        runningRepository =
            (application as RunningTrackerApplication)
                .runningRepository
        trackerEngine = RunningTrackerEngine(
            locationFilter = LocationFilter()
        )
        createNotificationChannel()
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
    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        when (intent?.action) {

            ACTION_START_TRACKING -> {
                startTracking()
            }

            ACTION_PAUSE_TRACKING -> {
                pauseTracking()
            }

            ACTION_RESUME_TRACKING -> {
                resumeTracking()
            }

            ACTION_STOP_TRACKING -> {
                stopTracking()
            }
        }
        return START_STICKY
    }
    private fun pauseTracking() {

        val newState = trackerEngine.pause()
        runningRepository.updateRunningState(newState)

        Log.d(
            TAG,
            "Run paused"
        )
    }
    private fun resumeTracking() {

        val newState = trackerEngine.resume()
        runningRepository.updateRunningState(newState)

        Log.d(
            TAG,
            "Run resumed"
        )
    }
    private fun startTracking() {

        if (trackingJob?.isActive == true) {
            return
        }

        trackerEngine.start()

        startForeground(
            NOTIFICATION_ID,
            createNotification()
        )

        trackingJob = serviceScope.launch {

            try {

                // Create ONE run
                runningRepository.startRun()

                locationTracker
                    .locationUpdates()
                    .collect { location ->

                        handleLocation(location)
                    }

            } catch (e: CancellationException) {

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
    private suspend fun handleLocation(
        location: LocationData
    ) {

        val state =
            trackerEngine.processLocation(location)
                ?: return

        runningRepository.updateRunningState(state)
        runningRepository.saveLocation(location)
        runningRepository.updateLocation(location)

        Log.d(
            TAG,
            "Location accepted: " +
                    "${location.latitude}, " +
                    "${location.longitude}"
        )
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
    }


    private fun stopTracking() {

        trackingJob?.cancel()
        trackingJob = null

        serviceScope.launch {

            try {
                runningRepository.finishRun()

                stopForeground(
                    STOP_FOREGROUND_REMOVE
                )

                stopSelf()

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "Failed to finish run",
                    e
                )
            }
        }
    }

    // notification methods...

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
        const val ACTION_PAUSE_TRACKING =
            "com.yourpackage.runningtracker.PAUSE_TRACKING"

        const val ACTION_RESUME_TRACKING =
            "com.yourpackage.runningtracker.RESUME_TRACKING"
        private const val CHANNEL_ID =
            "running_tracker_channel"

        private const val NOTIFICATION_ID =
            1001
    }
}