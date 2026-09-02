package com.varram.run
import org.osmdroid.config.Configuration
import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.varram.run.data.local.database.RunningDatabase
import com.varram.run.data.repository.RunningRepository

class RunningTrackerApplication : Application() {

    lateinit var database: RunningDatabase
        private set

    lateinit var runningRepository: RunningRepository
        private set

    override fun onCreate() {
        super.onCreate()

        Configuration.getInstance().userAgentValue =
            packageName

        database = Room.databaseBuilder(
            this,
            RunningDatabase::class.java,
            "running_database"
        )
            .addMigrations(MIGRATION_1_2)
            .build()

        runningRepository = RunningRepository(
            runDao = database.runDao(),
            locationPointDao = database.locationPointDao()
        )
    }
    val MIGRATION_1_2 = object : Migration(1, 2) {

        override fun migrate(db: SupportSQLiteDatabase) {

            db.execSQL("""
            ALTER TABLE runs
            ADD COLUMN durationMillis INTEGER NOT NULL DEFAULT 0
        """.trimIndent())

            db.execSQL("""
            ALTER TABLE runs
            ADD COLUMN distanceMeters REAL NOT NULL DEFAULT 0.0
        """.trimIndent())

            db.execSQL("""
            ALTER TABLE runs
            ADD COLUMN avgPaceSecondsPerKm REAL
        """.trimIndent())
        }
    }
}