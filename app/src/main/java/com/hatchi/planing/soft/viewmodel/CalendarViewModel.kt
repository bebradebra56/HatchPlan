package com.hatchi.planing.soft.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hatchi.planing.soft.data.entity.Batch
import com.hatchi.planing.soft.data.entity.Task
import com.hatchi.planing.soft.data.model.BatchStatus
import com.hatchi.planing.soft.data.repository.BatchRepository
import com.hatchi.planing.soft.data.repository.TaskRepository
import com.hatchi.planing.soft.util.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class CalendarEvent(
    val task: Task,
    val batch: Batch,
    val isPast: Boolean,
    val isToday: Boolean
)

data class CalendarUiState(
    val upcomingEvents: List<CalendarEvent> = emptyList(),
    val todayEvents: List<CalendarEvent> = emptyList(),
    val isLoading: Boolean = true
)

class CalendarViewModel(
    private val batchRepository: BatchRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadEvents()
    }

    private fun loadEvents() {
        viewModelScope.launch {
            combine(
                batchRepository.getBatchesByStatus(BatchStatus.ACTIVE),
                getUpcomingTasks()
            ) { batches, tasks ->
                val batchMap = batches.associateBy { it.id }
                val now = Date()
                
                val events = tasks.mapNotNull { task ->
                    val batch = batchMap[task.batchId]
                    if (batch != null) {
                        val isPast = task.dueDate.before(now) && !DateUtils.isToday(task.dueDate)
                        val isToday = DateUtils.isToday(task.dueDate)
                        CalendarEvent(task, batch, isPast, isToday)
                    } else null
                }

                val upcomingEvents = events.filter { !it.isPast }.sortedBy { it.task.dueDate }
                val todayEvents = events.filter { it.isToday }

                CalendarUiState(
                    upcomingEvents = upcomingEvents,
                    todayEvents = todayEvents,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun getUpcomingTasks(): Flow<List<Task>> {
        val futureDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 30) // Next 30 days
        }.time
        return taskRepository.getTasksDueBy(futureDate)
    }

    fun completeTask(task: Task) {
        viewModelScope.launch {
            taskRepository.completeTask(task)
        }
    }
}

