package com.hatchi.planing.soft.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hatchi.planing.soft.data.entity.Batch
import com.hatchi.planing.soft.data.entity.Task
import com.hatchi.planing.soft.data.model.BatchStatus
import com.hatchi.planing.soft.data.repository.BatchRepository
import com.hatchi.planing.soft.data.repository.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class HomeUiState(
    val activeBatches: List<Batch> = emptyList(),
    val todayTasks: List<Task> = emptyList(),
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val batchRepository: BatchRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                batchRepository.getBatchesByStatus(BatchStatus.ACTIVE),
                getTodayTasks()
            ) { batches, tasks ->
                HomeUiState(
                    activeBatches = batches,
                    todayTasks = tasks,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun getTodayTasks(): Flow<List<Task>> {
        val endOfToday = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }.time
        return taskRepository.getTasksDueBy(endOfToday)
    }

    fun completeTask(task: Task) {
        viewModelScope.launch {
            taskRepository.completeTask(task)
        }
    }

    fun getCurrentDay(batch: Batch): Int {
        return batchRepository.getCurrentDay(batch)
    }
}

