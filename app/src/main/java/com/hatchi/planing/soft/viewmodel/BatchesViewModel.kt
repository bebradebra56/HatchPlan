package com.hatchi.planing.soft.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hatchi.planing.soft.data.entity.Batch
import com.hatchi.planing.soft.data.model.BatchStatus
import com.hatchi.planing.soft.data.repository.BatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BatchesUiState(
    val allBatches: List<Batch> = emptyList(),
    val activeBatches: List<Batch> = emptyList(),
    val completedBatches: List<Batch> = emptyList(),
    val isLoading: Boolean = true
)

class BatchesViewModel(
    private val batchRepository: BatchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BatchesUiState())
    val uiState: StateFlow<BatchesUiState> = _uiState.asStateFlow()

    init {
        loadBatches()
    }

    private fun loadBatches() {
        viewModelScope.launch {
            batchRepository.getAllBatches().collect { batches ->
                _uiState.value = BatchesUiState(
                    allBatches = batches,
                    activeBatches = batches.filter { it.status == BatchStatus.ACTIVE },
                    completedBatches = batches.filter {
                        it.status == BatchStatus.COMPLETED || it.status == BatchStatus.FAILED
                    },
                    isLoading = false
                )
            }
        }
    }

    fun getCurrentDay(batch: Batch): Int {
        return batchRepository.getCurrentDay(batch)
    }

    fun deleteBatch(batch: Batch) {
        viewModelScope.launch {
            batchRepository.deleteBatch(batch)
        }
    }
}

