package com.hatchi.planing.soft.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hatchi.planing.soft.data.entity.Batch
import com.hatchi.planing.soft.data.model.BatchStatus
import com.hatchi.planing.soft.data.repository.BatchRepository
import com.hatchi.planing.soft.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReportsUiState(
    val totalBatches: Int = 0,
    val activeBatches: Int = 0,
    val completedBatches: Int = 0,
    val totalEggsIncubated: Int = 0,
    val totalEggsHatched: Int = 0,
    val successRate: Float = 0f,
    val recentBatches: List<Batch> = emptyList(),
    val isLoading: Boolean = true
)

class ReportsViewModel(
    private val batchRepository: BatchRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        loadReports()
    }

    private fun loadReports() {
        viewModelScope.launch {
            batchRepository.getAllBatches().collect { batches ->
                val completed = batches.filter { it.status == BatchStatus.COMPLETED }
                val totalEggs = batches.sumOf { it.totalEggs }
                val hatchedEggs = completed.sumOf { it.hatchedCount }
                val successRate = if (totalEggs > 0) (hatchedEggs.toFloat() / totalEggs.toFloat()) * 100 else 0f

                _uiState.value = ReportsUiState(
                    totalBatches = batches.size,
                    activeBatches = batches.count { it.status == BatchStatus.ACTIVE },
                    completedBatches = completed.size,
                    totalEggsIncubated = totalEggs,
                    totalEggsHatched = hatchedEggs,
                    successRate = successRate,
                    recentBatches = batches.take(5),
                    isLoading = false
                )
            }
        }
    }
}

