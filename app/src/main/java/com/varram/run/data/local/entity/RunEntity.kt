package com.varram.run.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "runs")
data class RunEntity(
    @PrimaryKey
    val runId: String,

    val startTime: Long,
    val endTime: Long? = null,
    val status: RunStatus,
    val durationMillis: Long = 0,
    val distanceMeters: Double = 0.0,

    val avgPaceSecondsPerKm: Double? = null,

    val isCompleted: Boolean = false
)
enum class RunStatus {
    ACTIVE,
    COMPLETED,
    CANCELLED
}
