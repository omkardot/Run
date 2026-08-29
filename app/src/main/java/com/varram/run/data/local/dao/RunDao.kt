package com.varram.run.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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
        LIMIT 1
    """)
    suspend fun getRun(runId: String): RunEntity?

    @Query("""
        SELECT * FROM runs
        ORDER BY startTime DESC
    """)
    fun observeRuns(): Flow<List<RunEntity>>

    @Query("""
        UPDATE runs
        SET endTime = :endTime,
            status = :status
        WHERE runId = :runId
    """)
    suspend fun finishRun(
        runId: String,
        endTime: Long,
        status: RunStatus
    )
}