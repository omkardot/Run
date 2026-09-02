package com.varram.run.data.model

import com.varram.run.data.local.entity.RunStatus
import org.osmdroid.util.GeoPoint

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val speed: Float,
    val bearing: Float,
    val altitude: Double,
    val timestamp: Long
)
data class RunningSession(
    val id: String,
    val startTime: Long,
    val endTime: Long? = null,
    val distanceMeters: Double = 0.0,
    val isRunning: Boolean = true
)

data class RunningState(
    val isTracking: Boolean = false,
    val isPaused: Boolean = false,
    val currentLocation: LocationData? = null,
    val distanceMeters: Double = 0.0,
    val elapsedTimeMillis: Long = 0L,
    val paceSecondsPerKm: Double? = null,
    val routePoints: List<RoutePoint> = emptyList()
)

data class RoutePoint(
    val latitude: Double,
    val longitude: Double
)