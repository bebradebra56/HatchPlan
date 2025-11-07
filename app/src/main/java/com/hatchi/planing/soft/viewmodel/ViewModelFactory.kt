package com.hatchi.planing.soft.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hatchi.planing.soft.data.database.HatchPlanDatabase
import com.hatchi.planing.soft.data.preferences.AppPreferences
import com.hatchi.planing.soft.data.repository.*

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    private val database by lazy { HatchPlanDatabase.getDatabase(context) }
    private val appPreferences by lazy { AppPreferences(context) }

    private val batchRepository by lazy {
        BatchRepository(database.batchDao(), database.taskDao())
    }
    private val presetRepository by lazy {
        PresetRepository(database.presetDao())
    }
    private val taskRepository by lazy {
        TaskRepository(database.taskDao())
    }
    private val readingRepository by lazy {
        ReadingRepository(database.readingDao())
    }
    private val deviceRepository by lazy {
        DeviceRepository(database.deviceDao())
    }
    private val noteRepository by lazy {
        NoteRepository(database.noteDao())
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(batchRepository, taskRepository) as T
            }
            modelClass.isAssignableFrom(BatchesViewModel::class.java) -> {
                BatchesViewModel(batchRepository) as T
            }
            modelClass.isAssignableFrom(CreateBatchViewModel::class.java) -> {
                CreateBatchViewModel(batchRepository, presetRepository) as T
            }
            modelClass.isAssignableFrom(ReportsViewModel::class.java) -> {
                ReportsViewModel(batchRepository, taskRepository) as T
            }
            modelClass.isAssignableFrom(PresetsViewModel::class.java) -> {
                PresetsViewModel(presetRepository) as T
            }
            modelClass.isAssignableFrom(CalendarViewModel::class.java) -> {
                CalendarViewModel(batchRepository, taskRepository) as T
            }
            modelClass.isAssignableFrom(AppViewModel::class.java) -> {
                AppViewModel(appPreferences) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    fun createBatchDetailViewModel(batchId: Long): BatchDetailViewModel {
        return BatchDetailViewModel(
            batchId,
            batchRepository,
            presetRepository,
            taskRepository,
            readingRepository,
            noteRepository
        )
    }
}

