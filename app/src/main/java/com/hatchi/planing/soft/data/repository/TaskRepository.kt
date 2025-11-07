package com.hatchi.planing.soft.data.repository

import com.hatchi.planing.soft.data.dao.TaskDao
import com.hatchi.planing.soft.data.entity.Task
import com.hatchi.planing.soft.data.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date

class TaskRepository(private val taskDao: TaskDao) {
    fun getTasksForBatch(batchId: Long): Flow<List<Task>> =
        taskDao.getTasksForBatch(batchId)

    fun getTasksDueBy(date: Date): Flow<List<Task>> =
        taskDao.getTasksDueBy(date)

    suspend fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)

    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun completeTask(task: Task) {
        val updatedTask = task.copy(
            status = TaskStatus.DONE,
            completedAt = Date()
        )
        taskDao.updateTask(updatedTask)
    }

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
}

