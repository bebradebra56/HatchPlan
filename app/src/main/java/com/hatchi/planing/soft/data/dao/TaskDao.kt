package com.hatchi.planing.soft.data.dao

import androidx.room.*
import com.hatchi.planing.soft.data.entity.Task
import com.hatchi.planing.soft.data.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE batchId = :batchId ORDER BY dueDate ASC")
    fun getTasksForBatch(batchId: Long): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE dueDate <= :date AND status = 'PENDING' ORDER BY dueDate ASC")
    fun getTasksDueBy(date: Date): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<Task>)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks WHERE batchId = :batchId")
    suspend fun deleteTasksForBatch(batchId: Long)
}

