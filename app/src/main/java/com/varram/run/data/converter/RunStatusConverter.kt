package com.varram.run.data.converter

import androidx.room.TypeConverter
import com.varram.run.data.local.entity.RunStatus

class RunStatusConverter {

    @TypeConverter
    fun fromStatus(
        status: RunStatus
    ): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(
        value: String
    ): RunStatus {
        return RunStatus.valueOf(value)
    }
}