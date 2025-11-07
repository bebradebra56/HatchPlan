package com.hatchi.planing.soft.data.repository

import com.hatchi.planing.soft.data.dao.PresetDao
import com.hatchi.planing.soft.data.entity.Preset
import com.hatchi.planing.soft.data.model.Species
import kotlinx.coroutines.flow.Flow

class PresetRepository(private val presetDao: PresetDao) {
    fun getAllPresets(): Flow<List<Preset>> = presetDao.getAllPresets()

    fun getPresetsBySpecies(species: Species): Flow<List<Preset>> =
        presetDao.getPresetsBySpecies(species)

    suspend fun getPresetById(id: Long): Preset? = presetDao.getPresetById(id)

    suspend fun insertPreset(preset: Preset): Long = presetDao.insertPreset(preset)

    suspend fun updatePreset(preset: Preset) = presetDao.updatePreset(preset)

    suspend fun deletePreset(preset: Preset) = presetDao.deletePreset(preset)
}

