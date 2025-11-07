package com.hatchi.planing.soft.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hatchi.planing.soft.data.converter.Converters
import com.hatchi.planing.soft.data.dao.*
import com.hatchi.planing.soft.data.entity.*
import com.hatchi.planing.soft.data.model.Species
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Preset::class,
        Batch::class,
        Task::class,
        Reading::class,
        Device::class,
        Note::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class HatchPlanDatabase : RoomDatabase() {
    abstract fun presetDao(): PresetDao
    abstract fun batchDao(): BatchDao
    abstract fun taskDao(): TaskDao
    abstract fun readingDao(): ReadingDao
    abstract fun deviceDao(): DeviceDao
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: HatchPlanDatabase? = null

        fun getDatabase(context: Context): HatchPlanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HatchPlanDatabase::class.java,
                    "hatchplan_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database)
                }
            }
        }

        suspend fun populateDatabase(database: HatchPlanDatabase) {
            val presetDao = database.presetDao()

            // Chicken preset
            presetDao.insertPreset(
                Preset(
                    name = "Chicken Standard",
                    species = Species.CHICKEN,
                    totalDays = 21,
                    turnsPerDay = 3,
                    stopTurnDay = 18,
                    stages = listOf(
                        IncubationStage(1, 18, 37.6f, 37.8f, 45f, 55f, "Main incubation period"),
                        IncubationStage(19, 21, 37.5f, 37.7f, 60f, 70f, "Hatching period")
                    ),
                    candleDays = listOf(7, 14, 18),
                    notes = "Standard chicken incubation protocol"
                )
            )

            // Duck preset
            presetDao.insertPreset(
                Preset(
                    name = "Duck Standard",
                    species = Species.DUCK,
                    totalDays = 28,
                    turnsPerDay = 3,
                    stopTurnDay = 26,
                    stages = listOf(
                        IncubationStage(1, 25, 37.4f, 37.6f, 50f, 55f, "Main incubation period"),
                        IncubationStage(26, 28, 37.3f, 37.5f, 65f, 75f, "Hatching period")
                    ),
                    candleDays = listOf(7, 14, 25),
                    requiresCooling = true,
                    requiresSpraying = true,
                    coolingStartDay = 14,
                    notes = "Daily cooling and spraying recommended from day 14"
                )
            )

            // Turkey preset
            presetDao.insertPreset(
                Preset(
                    name = "Turkey Standard",
                    species = Species.TURKEY,
                    totalDays = 28,
                    turnsPerDay = 3,
                    stopTurnDay = 26,
                    stages = listOf(
                        IncubationStage(1, 25, 37.4f, 37.6f, 50f, 55f, "Main incubation period"),
                        IncubationStage(26, 28, 37.3f, 37.5f, 65f, 70f, "Hatching period")
                    ),
                    candleDays = listOf(7, 14, 25),
                    notes = "Standard turkey incubation protocol"
                )
            )

            // Quail preset
            presetDao.insertPreset(
                Preset(
                    name = "Quail Standard",
                    species = Species.QUAIL,
                    totalDays = 18,
                    turnsPerDay = 3,
                    stopTurnDay = 15,
                    stages = listOf(
                        IncubationStage(1, 14, 37.6f, 37.8f, 45f, 55f, "Main incubation period"),
                        IncubationStage(15, 18, 37.5f, 37.7f, 65f, 70f, "Hatching period")
                    ),
                    candleDays = listOf(7, 14),
                    notes = "Quail eggs hatch quickly - monitor closely"
                )
            )

            // Goose preset
            presetDao.insertPreset(
                Preset(
                    name = "Goose Standard",
                    species = Species.GOOSE,
                    totalDays = 30,
                    turnsPerDay = 3,
                    stopTurnDay = 28,
                    stages = listOf(
                        IncubationStage(1, 27, 37.4f, 37.6f, 50f, 55f, "Main incubation period"),
                        IncubationStage(28, 30, 37.3f, 37.5f, 70f, 75f, "Hatching period")
                    ),
                    candleDays = listOf(7, 14, 27),
                    requiresCooling = true,
                    requiresSpraying = true,
                    coolingStartDay = 15,
                    notes = "Daily cooling and spraying from day 15"
                )
            )
        }
    }
}

