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

    private val routePoints =
        mutableListOf<RoutePoint>()

    fun start() {

        previousLocation = null
        totalDistanceMeters = 0.0

        startTimeMillis =
            SystemClock.elapsedRealtime()

        routePoints.clear()

        locationFilter.reset()
    }

    fun processLocation(
        location: LocationData
    ): RunningState? {

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

    fun stop() {

        previousLocation = null
        totalDistanceMeters = 0.0
        startTimeMillis = null

        routePoints.clear()

        locationFilter.reset()
    }

    private fun createRunningState(
        location: LocationData
    ): RunningState {

        val elapsedTimeMillis =
            SystemClock.elapsedRealtime() -
                    (startTimeMillis
                        ?: SystemClock.elapsedRealtime())

        return RunningState(
            isTracking = true,
            currentLocation = location,
            distanceMeters = totalDistanceMeters,
            elapsedTimeMillis = elapsedTimeMillis,
            routePoints = routePoints.toList()
        )
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