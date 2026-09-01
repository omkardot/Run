package com.varram.run.feature.details.presentation

import com.varram.run.data.local.entity.LocationPointEntity
import com.varram.run.data.local.entity.RunEntity

data class RunDetailsUiState(
    val run: RunEntity? = null,
    val routePoints: List<LocationPointEntity> = emptyList(),
    val isLoading: Boolean = true
)