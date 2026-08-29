package com.varram.run.data.model

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
    val currentLocation: LocationData? = null,
    val route: List<LocationData> = emptyList(),
    val distanceMeters: Double = 0.0
)