package com.varram.run.data.domain

import android.location.Location
import android.os.SystemClock
import com.varram.run.data.domain.tracker.LocationFilter
import com.varram.run.data.model.LocationData
import com.varram.run.data.model.RoutePoint
import com.varram.run.data.model.RunningState

/*

LocationTracker
      ↓
LocationTrackingService
      ↓
RunningTrackerEngine
      ↓
 ┌───────────────┐
 │ Filter        │
 │ Distance      │
 │ Duration      │
 │ Pace          │
 └───────────────┘
      ↓
RunningRepository
      ↓
 ┌──────────┐
 │ Room     │
 │ StateFlow│
 └──────────┘

 */

class RunningTrackerEngine(
    private val locationFilter: LocationFilter
) {

    companion object {
        private const val MIN_MOVEMENT_METERS = 3.0
    }

    private var previousLocation: LocationData? = null

    private var totalDistanceMeters = 0.0

    private var startTimeMillis: Long? = null
    private var pausedAtMillis: Long? = null
    private var totalPausedMillis = 0L
    private val routePoints =
        mutableListOf<RoutePoint>()

    fun start() {

        previousLocation = null
        totalDistanceMeters = 0.0

        startTimeMillis =
            SystemClock.elapsedRealtime()

        routePoints.clear()
        pausedAtMillis = null
        totalPausedMillis = 0L
        locationFilter.reset()
    }
    fun stop() {

        previousLocation = null

        totalDistanceMeters = 0.0

        startTimeMillis = null

        pausedAtMillis = null

        totalPausedMillis = 0L

        routePoints.clear()

        locationFilter.reset()
    }
    fun pause(): RunningState {

        if (startTimeMillis == null) {
            start()
        }

        if (pausedAtMillis == null) {
            pausedAtMillis =
                SystemClock.elapsedRealtime()
        }

        return getCurrentState()
    }
    fun resume(): RunningState {

        val pausedAt = pausedAtMillis
        if (pausedAt != null) {
            totalPausedMillis +=
                SystemClock.elapsedRealtime() - pausedAt
        }

        pausedAtMillis = null

        // Don't calculate distance
        // across the pause period.
        previousLocation = null

        locationFilter.reset()

        return getCurrentState()
    }
    fun processLocation(
        location: LocationData
    ): RunningState? {
        if (pausedAtMillis != null) {
            return null
        }
        // Reject bad GPS
        if (!locationFilter.accept(location)) {
            return null
        }

        // Start if necessary
        if (startTimeMillis == null) {
            start()
        }

        val previous = previousLocation

        // First valid location
        if (previous == null) {

            previousLocation = location

            routePoints.add(
                RoutePoint(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            )

            return createRunningState(location)
        }

        // Distance from previous accepted point
        val distance = calculateDistance(
            previous,
            location
        )

        // Ignore movement smaller than 3 meters
        if (distance < MIN_MOVEMENT_METERS) {

            return createRunningState(location)
        }

        // Real movement
        totalDistanceMeters += distance

        previousLocation = location

        // Add point to route
        routePoints.add(
            RoutePoint(
                latitude = location.latitude,
                longitude = location.longitude
            )
        )

        return createRunningState(location)
    }

    private fun createRunningState(
        location: LocationData?
    ): RunningState {

        val elapsedTimeMillis =
            getElapsedTime()
        val pace = if (location != null) {
            calculatePace(
                distanceMeters = totalDistanceMeters,
                elapsedTimeMillis = elapsedTimeMillis
            )
        } else null

        return RunningState(
            isTracking = startTimeMillis != null,
            currentLocation = location,
            distanceMeters = totalDistanceMeters,
            elapsedTimeMillis = elapsedTimeMillis,
            paceSecondsPerKm = pace,
            isPaused = pausedAtMillis != null,
            routePoints = routePoints.toList()
        )
    }
    fun getCurrentState(): RunningState {
        return createRunningState(previousLocation)
    }
    private fun getElapsedTime(): Long {

        val start =
            startTimeMillis
                ?: return 0L

        val now =
            SystemClock.elapsedRealtime()

        val currentPauseDuration =
            pausedAtMillis?.let {
                now - it
            } ?: 0L

        return now -
                start -
                totalPausedMillis -
                currentPauseDuration
    }
    private fun calculatePace(
        distanceMeters: Double,
        elapsedTimeMillis: Long
    ): Double? {

        if (distanceMeters < 10.0) {
            return null
        }

        if (elapsedTimeMillis <= 0) {
            return null
        }

        val distanceKm = distanceMeters / 1000.0
        val elapsedSeconds = elapsedTimeMillis / 1000.0

        return elapsedSeconds / distanceKm
    }
    private fun calculateDistance(
        previous: LocationData,
        current: LocationData
    ): Double {

        val results = FloatArray(1)

        Location.distanceBetween(
            previous.latitude,
            previous.longitude,
            current.latitude,
            current.longitude,
            results
        )

        return results[0].toDouble()
    }
}