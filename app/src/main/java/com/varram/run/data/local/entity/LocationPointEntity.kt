package com.varram.run.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "location_points",
    indices = [
        Index(value = ["runId"]),
        Index(value = ["timestamp"])
    ]
)
data class LocationPointEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val runId: String,

    val latitude: Double,

    val longitude: Double,

    val accuracy: Float,

    val speed: Float,

    val bearing: Float,

    val altitude: Double,

    val timestamp: Long
)