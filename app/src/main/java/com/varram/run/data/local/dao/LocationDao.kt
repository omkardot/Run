package com.varram.run.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.varram.run.data.local.entity.LocationPointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationPointDao {

    @Insert
    suspend fun insertPoint(
        point: LocationPointEntity
    )

    @Insert
    suspend fun insertPoints(
        points: List<LocationPointEntity>
    )

    @Query("""
        SELECT * FROM location_points
        WHERE runId = :runId
        ORDER BY timestamp ASC
    """)
    fun observePoints(
        runId: String
    ): Flow<List<LocationPointEntity>>

    @Query("""
        SELECT * FROM location_points
        WHERE runId = :runId
        ORDER BY timestamp ASC
    """)
    suspend fun getPoints(
        runId: String
    ): List<LocationPointEntity>

    @Query("""
        DELETE FROM location_points
        WHERE runId = :runId
    """)
    suspend fun deletePointsForRun(
        runId: String
    )
}