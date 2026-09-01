package com.varram.run.data.local.database

import android.content.Context
import android.location.Location
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.varram.run.data.converter.RunStatusConverter
import com.varram.run.data.local.dao.LocationPointDao
import com.varram.run.data.local.dao.RunDao
import com.varram.run.data.local.entity.LocationPointEntity
import com.varram.run.data.local.entity.RunEntity


@Database(
    entities = [
        RunEntity::class,
        LocationPointEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(RunStatusConverter::class)
abstract class RunningDatabase : RoomDatabase() {

    abstract fun runDao(): RunDao

    abstract fun locationPointDao(): LocationPointDao

    companion object {

        private const val DATABASE_NAME =
            "running_tracker.db"
    }
}