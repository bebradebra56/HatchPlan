package com.hatchi.planing.soft.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hatchi.planing.soft.data.entity.Batch
import com.hatchi.planing.soft.data.entity.Preset
import com.hatchi.planing.soft.data.model.Species
import com.hatchi.planing.soft.data.repository.BatchRepository
import com.hatchi.planing.soft.data.repository.PresetRepository
import com.hatchi.planing.soft.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

data class CreateBatchUiState(
    val step: Int = 1,
    val species: Species? = null,
    val presets: List<Preset> = emptyList(),
    val selectedPreset: Preset? = null,
    val batchName: String = "",
    val totalEggs: String = "",
    val startDate: Date = Date(),
    val notes: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class CreateBatchViewModel(
    private val batchRepository: BatchRepository,
    private val presetRepository: PresetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateBatchUiState())
    val uiState: StateFlow<CreateBatchUiState> = _uiState.asStateFlow()

    init {
        loadPresets()
    }

    private fun loadPresets() {
        viewModelScope.launch {
            presetRepository.getAllPresets().collect { presets ->
                _uiState.value = _uiState.value.copy(presets = presets)
            }
        }
    }

    fun selectSpecies(species: Species) {
        val presets = _uiState.value.presets.filter { it.species == species }
        val defaultPreset = presets.firstOrNull()
        
        _uiState.value = _uiState.value.copy(
            species = species,
            selectedPreset = defaultPreset,
            batchName = if (_uiState.value.batchName.isEmpty()) 
                "${species.name.lowercase().capitalize()} Batch ${System.currentTimeMillis() % 1000}"
            else _uiState.value.batchName
        )
    }

    fun selectPreset(preset: Preset) {
        _uiState.value = _uiState.value.copy(selectedPreset = preset)
    }

    fun updateBatchName(name: String) {
        _uiState.value = _uiState.value.copy(batchName = name)
    }

    fun updateTotalEggs(eggs: String) {
        _uiState.value = _uiState.value.copy(totalEggs = eggs)
    }

    fun updateStartDate(date: Date) {
        _uiState.value = _uiState.value.copy(startDate = date)
    }

    fun updateNotes(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    fun nextStep() {
        _uiState.value = _uiState.value.copy(step = _uiState.value.step + 1)
    }

    fun previousStep() {
        if (_uiState.value.step > 1) {
            _uiState.value = _uiState.value.copy(step = _uiState.value.step - 1)
        }
    }

    fun createBatch(onSuccess: () -> Unit) {
        val state = _uiState.value
        val species = state.species ?: return
        val preset = state.selectedPreset ?: return

        if (state.batchName.isEmpty()) {
            _uiState.value = state.copy(error = "Please enter a batch name")
            return
        }

        val eggsCount = state.totalEggs.toIntOrNull() ?: 0
        if (eggsCount <= 0) {
            _uiState.value = state.copy(error = "Please enter a valid number of eggs")
            return
        }

        _uiState.value = state.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val expectedHatchDate = DateUtils.addDays(state.startDate, preset.totalDays)
                
                val batch = Batch(
                    name = state.batchName,
                    species = species,
                    presetId = preset.id,
                    startDate = state.startDate,
                    totalEggs = eggsCount,
                    expectedHatchDate = expectedHatchDate,
                    notes = state.notes
                )

                batchRepository.insertBatch(batch, preset)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to create batch: ${e.message}"
                )
            }
        }
    }
}

