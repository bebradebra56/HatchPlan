package com.hatchi.planing.soft.data.repository

import com.hatchi.planing.soft.data.dao.BatchDao
import com.hatchi.planing.soft.data.dao.TaskDao
import com.hatchi.planing.soft.data.entity.Batch
import com.hatchi.planing.soft.data.entity.Preset
import com.hatchi.planing.soft.data.entity.Task
import com.hatchi.planing.soft.data.model.BatchStatus
import com.hatchi.planing.soft.data.model.TaskStatus
import com.hatchi.planing.soft.data.model.TaskType
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.Date

class BatchRepository(
    private val batchDao: BatchDao,
    private val taskDao: TaskDao
) {
    fun getAllBatches(): Flow<List<Batch>> = batchDao.getAllBatches()

    fun getBatchesByStatus(status: BatchStatus): Flow<List<Batch>> =
        batchDao.getBatchesByStatus(status)

    suspend fun getBatchById(id: Long): Batch? = batchDao.getBatchById(id)

    fun getBatchByIdFlow(id: Long): Flow<Batch?> = batchDao.getBatchByIdFlow(id)

    suspend fun insertBatch(batch: Batch, preset: Preset): Long {
        val batchId = batchDao.insertBatch(batch)
        generateTasksForBatch(batchId, batch, preset)
        return batchId
    }

    suspend fun updateBatch(batch: Batch) = batchDao.updateBatch(batch)

    suspend fun deleteBatch(batch: Batch) {
        taskDao.deleteTasksForBatch(batch.id)
        batchDao.deleteBatch(batch)
    }

    private suspend fun generateTasksForBatch(batchId: Long, batch: Batch, preset: Preset) {
        val tasks = mutableListOf<Task>()
        val calendar = Calendar.getInstance()
        calendar.time = batch.startDate

        // Generate turn tasks
        for (day in 1..preset.stopTurnDay) {
            calendar.time = batch.startDate
            calendar.add(Calendar.DAY_OF_MONTH, day - 1)
            
            for (turn in 1..preset.turnsPerDay) {
                val hour = when (turn) {
                    1 -> 8  // Morning
                    2 -> 14 // Afternoon
                    else -> 20 // Evening
                }
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, 0)
                
                tasks.add(
                    Task(
                        batchId = batchId,
                        type = TaskType.TURN,
                        dueDate = calendar.time,
                        status = TaskStatus.PENDING
                    )
                )
            }
        }

        // Generate stop turn task
        calendar.time = batch.startDate
        calendar.add(Calendar.DAY_OF_MONTH, preset.stopTurnDay)
        calendar.set(Calendar.HOUR_OF_DAY, 20)
        calendar.set(Calendar.MINUTE, 0)
        tasks.add(
            Task(
                batchId = batchId,
                type = TaskType.STOP_TURN,
                dueDate = calendar.time,
                status = TaskStatus.PENDING
            )
        )

        // Generate candling tasks
        preset.candleDays.forEach { day ->
            calendar.time = batch.startDate
            calendar.add(Calendar.DAY_OF_MONTH, day - 1)
            calendar.set(Calendar.HOUR_OF_DAY, 19)
            calendar.set(Calendar.MINUTE, 0)
            tasks.add(
                Task(
                    batchId = batchId,
                    type = TaskType.CANDLE,
                    dueDate = calendar.time,
                    status = TaskStatus.PENDING
                )
            )
        }

        // Generate cooling tasks if required
        if (preset.requiresCooling && preset.coolingStartDay != null) {
            for (day in preset.coolingStartDay..preset.stopTurnDay) {
                calendar.time = batch.startDate
                calendar.add(Calendar.DAY_OF_MONTH, day - 1)
                calendar.set(Calendar.HOUR_OF_DAY, 12)
                calendar.set(Calendar.MINUTE, 0)
                tasks.add(
                    Task(
                        batchId = batchId,
                        type = TaskType.COOL,
                        dueDate = calendar.time,
                        status = TaskStatus.PENDING
                    )
                )
            }
        }

        // Generate spraying tasks if required
        if (preset.requiresSpraying && preset.coolingStartDay != null) {
            for (day in preset.coolingStartDay..preset.stopTurnDay) {
                calendar.time = batch.startDate
                calendar.add(Calendar.DAY_OF_MONTH, day - 1)
                calendar.set(Calendar.HOUR_OF_DAY, 12)
                calendar.set(Calendar.MINUTE, 30)
                tasks.add(
                    Task(
                        batchId = batchId,
                        type = TaskType.SPRAY,
                        dueDate = calendar.time,
                        status = TaskStatus.PENDING
                    )
                )
            }
        }

        // Generate ventilation tasks (every 3 days)
        for (day in 1..preset.totalDays step 3) {
            calendar.time = batch.startDate
            calendar.add(Calendar.DAY_OF_MONTH, day - 1)
            calendar.set(Calendar.HOUR_OF_DAY, 10)
            calendar.set(Calendar.MINUTE, 0)
            tasks.add(
                Task(
                    batchId = batchId,
                    type = TaskType.VENTILATION,
                    dueDate = calendar.time,
                    status = TaskStatus.PENDING
                )
            )
        }

        // Generate water refill tasks (every 5 days)
        for (day in 1..preset.totalDays step 5) {
            calendar.time = batch.startDate
            calendar.add(Calendar.DAY_OF_MONTH, day - 1)
            calendar.set(Calendar.HOUR_OF_DAY, 9)
            calendar.set(Calendar.MINUTE, 0)
            tasks.add(
                Task(
                    batchId = batchId,
                    type = TaskType.ADD_WATER,
                    dueDate = calendar.time,
                    status = TaskStatus.PENDING
                )
            )
        }

        // Generate hatch task
        calendar.time = batch.expectedHatchDate
        calendar.set(Calendar.HOUR_OF_DAY, 8)
        calendar.set(Calendar.MINUTE, 0)
        tasks.add(
            Task(
                batchId = batchId,
                type = TaskType.HATCH,
                dueDate = calendar.time,
                status = TaskStatus.PENDING
            )
        )

        taskDao.insertTasks(tasks)
    }

    fun getCurrentDay(batch: Batch): Int {
        val now = Date()
        val diff = now.time - batch.startDate.time
        return (diff / (1000 * 60 * 60 * 24)).toInt() + 1
    }
}

