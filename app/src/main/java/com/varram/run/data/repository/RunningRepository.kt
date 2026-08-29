package com.varram.run.data.repository

import android.util.Log
import com.varram.run.data.local.dao.LocationPointDao
import com.varram.run.data.local.dao.RunDao
import com.varram.run.data.local.entity.LocationPointEntity
import com.varram.run.data.local.entity.RunEntity
import com.varram.run.data.local.entity.RunStatus
import com.varram.run.data.model.LocationData
import com.varram.run.data.model.RunningState
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

    val runningState: StateFlow<RunningState> =
        _runningState.asStateFlow()

    fun updateLocation(location: LocationData) {

        _runningState.update {
            it.copy(
                currentLocation = location
            )
        }
    }
    init {
        Log.d(
            "RunningRepository",
            "Repository instance: ${System.identityHashCode(this)}"
        )
    }
    private var activeRunId: String? = null

    suspend fun startRun(): String {

        val runId = UUID.randomUUID().toString()

        val run = RunEntity(
            runId = runId,
            startTime = System.currentTimeMillis(),
            status = RunStatus.ACTIVE
        )

        runDao.insertRun(run)

        activeRunId = runId

        return runId
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
            bearing = location.bearing,
            altitude = location.altitude,
            timestamp = location.timestamp
        )

        locationPointDao.insertPoint(entity)
    }

    suspend fun finishRun() {

        val runId = activeRunId ?: return

        runDao.finishRun(
            runId = runId,
            endTime = System.currentTimeMillis(),
            status = RunStatus.COMPLETED
        )

        activeRunId = null
    }
}