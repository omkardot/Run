package com.varram.run.data.repository

import android.R.attr.action
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.varram.run.data.domain.tracker.LocationFilter
import com.varram.run.data.local.dao.LocationPointDao
import com.varram.run.data.local.dao.RunDao
import com.varram.run.data.local.entity.LocationPointEntity
import com.varram.run.data.local.entity.RunEntity
import com.varram.run.data.local.entity.RunStatus
import com.varram.run.data.model.LocationData
import com.varram.run.data.model.RunningState
import com.varram.run.service.LocationTrackingService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class RunningRepository(
    private val runDao: RunDao,
    private val locationPointDao: LocationPointDao
) {
    private val _runningState =
        MutableStateFlow(RunningState())
    private val locationFilter = LocationFilter()
    val runningState: StateFlow<RunningState> =
        _runningState.asStateFlow()

    fun updateRunningState(state: RunningState) {
        _runningState.value = state
    }

    fun setIsPaused(isPaused: Boolean) {
        _runningState.update {
            it.copy(isPaused = isPaused)
        }
    }

    fun updateLocation(location: LocationData) {

        _runningState.update {
            it.copy(
                currentLocation = location
            )
        }
    }

    private var activeRunId: String? = null

    suspend fun startRun(): String {

        val runId = UUID.randomUUID().toString()

        locationFilter.reset()
        val run = RunEntity(
            runId = runId,
            startTime = System.currentTimeMillis(),
            status = RunStatus.RUNNING
        )

        _runningState.update {
            it.copy(
                isTracking = true
            )
        }
        runDao.insertRun(run)

        activeRunId = runId

        return runId
    }

    fun getCompletedRuns(): Flow<List<RunEntity>> {
        return runDao.getRunsByStatus(RunStatus.COMPLETED)
    }

    suspend fun saveLocation(
        location: LocationData
    ) {

        val runId = activeRunId ?: return

        val entity = LocationPointEntity(
            runId = runId,
            latitude = location.latitude,
            longitude = location.longitude,
            accuracy = location.accuracy,
            speed = location.speed,
            altitude = location.altitude,
            timestamp = location.timestamp
        )

        locationPointDao.insertPoint(entity)
    }

    fun getRun(
        runId: String
    ): Flow<RunEntity?> {
        return runDao.getRun(runId)
    }

    fun getLocationPoints(
        runId: String
    ): Flow<List<LocationPointEntity>> {
        return locationPointDao.getPointsForRun(runId)
    }

    suspend fun finishRun() {

        val runId = activeRunId ?: return

        val finalState = _runningState.value
        runDao.finishRun(
            runId = runId,
            endTime = System.currentTimeMillis(),
            status = RunStatus.COMPLETED,
            durationMillis = finalState.elapsedTimeMillis,
            distanceMeters = finalState.distanceMeters,
            avgPaceSecondsPerKm = finalState.paceSecondsPerKm
        )
        _runningState.update {
            it.copy(
                isTracking = false,
                isPaused = false
            )
        }
        activeRunId = null
    }
}