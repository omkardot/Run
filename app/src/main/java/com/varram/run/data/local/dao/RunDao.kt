package com.varram.run.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.varram.run.data.local.entity.LocationPointEntity
import com.varram.run.data.local.entity.RunEntity
import com.varram.run.data.local.entity.RunStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: RunEntity)

    @Query("""
    SELECT * FROM runs
    WHERE runId = :runId
""")
     fun getRun(
        runId: String
    ): Flow<RunEntity?>

    @Query("""
        SELECT * FROM runs
        ORDER BY startTime DESC
    """)
    fun observeRuns(): Flow<List<RunEntity>>
    @Query("""
    SELECT * FROM location_points
    WHERE runId = :runId
    ORDER BY timestamp ASC
""")
    fun getPointsForRun(
        runId: String
    ): Flow<List<LocationPointEntity>>
    @Query("""
    UPDATE runs
    SET endTime = :endTime,
        durationMillis = :durationMillis,
        distanceMeters = :distanceMeters,
        avgPaceSecondsPerKm = :avgPaceSecondsPerKm,
        status = :status
    WHERE runId = :runId
""")

    suspend fun finishRun(
        runId: String,
        endTime: Long,
        durationMillis: Long,
        distanceMeters: Double,
        avgPaceSecondsPerKm: Double?,
        status: RunStatus
    )

    @Query("""
    SELECT * FROM runs
    WHERE status = :status
    ORDER BY startTime DESC
""")
    fun getRunsByStatus(
        status: RunStatus = RunStatus.COMPLETED
    ): Flow<List<RunEntity>>
}