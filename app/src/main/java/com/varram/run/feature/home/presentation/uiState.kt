package com.varram.run.feature.home.presentation

import com.varram.run.core.location.LocationTracker
import com.varram.run.data.model.LocationData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RunningTrackerUiState(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val accuracy: Float? = null,
    var isTracking: Boolean = false
)
