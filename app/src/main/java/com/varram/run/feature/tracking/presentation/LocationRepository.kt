package com.varram.run.feature.tracking.presentation

import com.varram.run.data.model.LocationData
import com.varram.run.data.model.RunningState
import com.varram.run.data.repository.RunningRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LocationRepository {
    private val _runningState =
        MutableStateFlow(RunningState())
    lateinit var runningRepository: RunningRepository
        private set
    val runningState: StateFlow<RunningState> =
        _runningState.asStateFlow()
    private val _currentLocation =
        MutableStateFlow<LocationData?>(null)

    val currentLocation: StateFlow<LocationData?> =
        _currentLocation.asStateFlow()

    fun updateLocation(location: LocationData) {
        _currentLocation.value = location
    }


    fun startRun() {
        _runningState.update {
            it.copy(
                isTracking = true
            )
        }
    }

    fun stopRun() {
        _runningState.update {
            it.copy(
                isTracking = false
            )
        }
    }

    fun processLocation(location: LocationData) {
        // GPS filtering
        // distance calculation
        // route update
    }
}