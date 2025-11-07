package com.hatchi.planing.soft.data.dao

import androidx.room.*
import com.hatchi.planing.soft.data.entity.Preset
import com.hatchi.planing.soft.data.model.Species
import kotlinx.coroutines.flow.Flow

@Dao
interface PresetDao {
    @Query("SELECT * FROM presets")
    fun getAllPresets(): Flow<List<Preset>>

    @Query("SELECT * FROM presets WHERE id = :id")
    suspend fun getPresetById(id: Long): Preset?

    @Query("SELECT * FROM presets WHERE species = :species")
    fun getPresetsBySpecies(species: Species): Flow<List<Preset>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: Preset): Long

    @Update
    suspend fun updatePreset(preset: Preset)

    @Delete
    suspend fun deletePreset(preset: Preset)
}

