package com.varram.run.feature.home.presentation

import com.varram.run.core.location.LocationTracker
import com.varram.run.data.model.LocationData
import com.varram.run.data.model.RoutePoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RunningTrackerUiState(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val accuracy: Float? = null,
    val isTracking: Boolean = false,
    val distanceMeters: Double = 0.0,
    val elapsedTimeMillis: Long = 0L,
    val paceSecondsPerKm: Double? = null,
    val routePoints: List<RoutePoint> = emptyList()
)