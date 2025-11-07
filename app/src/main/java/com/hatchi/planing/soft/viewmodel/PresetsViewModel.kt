package com.hatchi.planing.soft.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hatchi.planing.soft.data.entity.Preset
import com.hatchi.planing.soft.data.repository.PresetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PresetsUiState(
    val presets: List<Preset> = emptyList(),
    val isLoading: Boolean = true
)

class PresetsViewModel(
    private val presetRepository: PresetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PresetsUiState())
    val uiState: StateFlow<PresetsUiState> = _uiState.asStateFlow()

    init {
        loadPresets()
    }

    private fun loadPresets() {
        viewModelScope.launch {
            presetRepository.getAllPresets().collect { presets ->
                _uiState.value = PresetsUiState(
                    presets = presets,
                    isLoading = false
                )
            }
        }
    }
}

