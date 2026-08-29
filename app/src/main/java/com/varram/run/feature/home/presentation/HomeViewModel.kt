package com.varram.run.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


class RunningTrackerViewModel : ViewModel {

    private val repository: LocationRepository

    constructor(repository: LocationRepository) : super() {
        this.repository = repository
        this.uiState = repository.currentLocation
            .map { location ->

                RunningTrackerUiState(
                    latitude = location?.latitude,
                    longitude = location?.longitude,
                    accuracy = location?.accuracy
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = RunningTrackerUiState()
            )
    }

    val uiState: StateFlow<RunningTrackerUiState>
}