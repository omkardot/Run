package com.varram.run.feature.history.presentation

import com.varram.run.data.local.entity.RunEntity

data class HistoryUiState(
    val runs: List<RunEntity> = emptyList(),
    val isLoading: Boolean = true,
    val hasMoreRuns: Boolean = false
)