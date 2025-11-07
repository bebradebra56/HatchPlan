package com.hatchi.planing.soft.data.dao

import androidx.room.*
import com.hatchi.planing.soft.data.entity.Reading
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ReadingDao {
    @Query("SELECT * FROM readings WHERE batchId = :batchId ORDER BY timestamp DESC")
    fun getReadingsForBatch(batchId: Long): Flow<List<Reading>>

    @Query("SELECT * FROM readings WHERE batchId = :batchId AND timestamp BETWEEN :start AND :end ORDER BY timestamp ASC")
    fun getReadingsInRange(batchId: Long, start: Date, end: Date): Flow<List<Reading>>

    @Query("SELECT * FROM readings WHERE id = :id")
    suspend fun getReadingById(id: Long): Reading?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: Reading): Long

    @Update
    suspend fun updateReading(reading: Reading)

    @Delete
    suspend fun deleteReading(reading: Reading)

    @Query("DELETE FROM readings WHERE batchId = :batchId")
    suspend fun deleteReadingsForBatch(batchId: Long)
}

