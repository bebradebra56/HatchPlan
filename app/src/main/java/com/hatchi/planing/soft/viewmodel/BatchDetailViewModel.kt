package com.hatchi.planing.soft.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hatchi.planing.soft.data.entity.Batch
import com.hatchi.planing.soft.data.entity.Note
import com.hatchi.planing.soft.data.entity.Preset
import com.hatchi.planing.soft.data.entity.Reading
import com.hatchi.planing.soft.data.entity.Task
import com.hatchi.planing.soft.data.repository.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date

data class BatchDetailUiState(
    val batch: Batch? = null,
    val preset: Preset? = null,
    val tasks: List<Task> = emptyList(),
    val readings: List<Reading> = emptyList(),
    val notes: List<Note> = emptyList(),
    val currentDay: Int = 0,
    val isLoading: Boolean = true
)

class BatchDetailViewModel(
    private val batchId: Long,
    private val batchRepository: BatchRepository,
    private val presetRepository: PresetRepository,
    private val taskRepository: TaskRepository,
    private val readingRepository: ReadingRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BatchDetailUiState())
    val uiState: StateFlow<BatchDetailUiState> = _uiState.asStateFlow()

    init {
        loadBatchDetails()
    }

    private fun loadBatchDetails() {
        viewModelScope.launch {
            combine(
                batchRepository.getBatchByIdFlow(batchId),
                taskRepository.getTasksForBatch(batchId),
                readingRepository.getReadingsForBatch(batchId),
                noteRepository.getNotesForBatch(batchId)
            ) { batch, tasks, readings, notes ->
                val preset = batch?.presetId?.let { presetRepository.getPresetById(it) }
                val currentDay = batch?.let { batchRepository.getCurrentDay(it) } ?: 0

                BatchDetailUiState(
                    batch = batch,
                    preset = preset,
                    tasks = tasks,
                    readings = readings,
                    notes = notes,
                    currentDay = currentDay,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun completeTask(task: Task) {
        viewModelScope.launch {
            taskRepository.completeTask(task)
        }
    }

    fun addReading(temperature: Float, humidity: Float, notes: String = "") {
        viewModelScope.launch {
            val reading = Reading(
                batchId = batchId,
                timestamp = Date(),
                temperature = temperature,
                humidity = humidity,
                source = "manual",
                notes = notes
            )
            readingRepository.insertReading(reading)
        }
    }

    fun addNote(content: String, imageUri: String? = null) {
        viewModelScope.launch {
            val note = Note(
                batchId = batchId,
                timestamp = Date(),
                day = _uiState.value.currentDay,
                content = content,
                imageUri = imageUri
            )
            noteRepository.insertNote(note)
        }
    }

    fun getTasksForDay(day: Int): List<Task> {
        val batch = _uiState.value.batch ?: return emptyList()
        val targetDate = Date(batch.startDate.time + (day - 1) * 24 * 60 * 60 * 1000L)
        
        return _uiState.value.tasks.filter { task ->
            val taskDay = ((task.dueDate.time - batch.startDate.time) / (24 * 60 * 60 * 1000L)).toInt() + 1
            taskDay == day
        }
    }
}

