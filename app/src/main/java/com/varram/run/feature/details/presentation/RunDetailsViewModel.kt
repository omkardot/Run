package com.varram.run.feature.details.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.varram.run.data.repository.RunningRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class RunDetailsViewModel(
    private val repository: RunningRepository,
    private val runId: String
) : ViewModel() {

    val uiState: StateFlow<RunDetailsUiState> =
        combine(
            repository.getRun(runId),
            repository.getLocationPoints(runId)
        ) { run, points ->
            RunDetailsUiState(
                run = run,
                routePoints = points,
                isLoading = false
            )

        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RunDetailsUiState()
        )
}
class RunDetailsViewModelFactory(
    private val repository: RunningRepository,
    private val runId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                RunDetailsViewModel::class.java
            )
        ) {

            @Suppress("UNCHECKED_CAST")
            return RunDetailsViewModel(
                repository = repository,
                runId = runId
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}