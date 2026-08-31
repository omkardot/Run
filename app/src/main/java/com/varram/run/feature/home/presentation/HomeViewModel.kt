package com.varram.run.feature.home.presentation

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.varram.run.data.local.entity.RunStatus
import com.varram.run.data.repository.RunningRepository
import com.varram.run.feature.tracking.presentation.LocationRepository
import com.varram.run.service.LocationTrackingService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.jvm.java


class RunningTrackerViewModel(
    private val repository: RunningRepository
) : ViewModel() {

    val uiState: StateFlow<RunningTrackerUiState> =
        repository.runningState
            .map { state ->

                RunningTrackerUiState(
                    latitude = state.currentLocation?.latitude,
                    longitude = state.currentLocation?.longitude,
                    accuracy = state.currentLocation?.accuracy,
                    isTracking = state.isTracking,
                    distanceMeters = state.distanceMeters,
                    elapsedTimeMillis = state.elapsedTimeMillis,
                    routePoints = state.routePoints
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = RunningTrackerUiState()
            )

    fun stopTracking(context: Context) {
        Log.d("ViewModel ","Stop Location is clicked")
        val intent = Intent(
            context,
            LocationTrackingService::class.java
        ).apply {
            action =
                LocationTrackingService.ACTION_STOP_TRACKING
        }

        context.startService(intent)
    }

    fun startTracking(context: Context) {

        Log.d("ViewModel ","Start Location is clicked")
        val intent = Intent(
            context,
            LocationTrackingService::class.java
        ).apply {
            action =
                LocationTrackingService.ACTION_START_TRACKING
        }

        ContextCompat.startForegroundService(
            context,
            intent
        )
    }
}