package com.hatchi.planing.soft.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hatchi.planing.soft.data.preferences.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AppUiState(
    val isFirstLaunch: Boolean = true,
    val isLoading: Boolean = true
)

class AppViewModel(
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    init {
        checkFirstLaunch()
    }

    private fun checkFirstLaunch() {
        viewModelScope.launch {
            appPreferences.isFirstLaunch.collect { isFirstLaunch ->
                _uiState.value = AppUiState(
                    isFirstLaunch = isFirstLaunch,
                    isLoading = false
                )
            }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            appPreferences.setFirstLaunchCompleted()
        }
    }
}
