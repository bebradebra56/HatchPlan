package com.hatchi.planing.soft.data.repository

import com.hatchi.planing.soft.data.dao.ReadingDao
import com.hatchi.planing.soft.data.entity.Reading
import kotlinx.coroutines.flow.Flow
import java.util.Date

class ReadingRepository(private val readingDao: ReadingDao) {
    fun getReadingsForBatch(batchId: Long): Flow<List<Reading>> =
        readingDao.getReadingsForBatch(batchId)

    fun getReadingsInRange(batchId: Long, start: Date, end: Date): Flow<List<Reading>> =
        readingDao.getReadingsInRange(batchId, start, end)

    suspend fun getReadingById(id: Long): Reading? = readingDao.getReadingById(id)

    suspend fun insertReading(reading: Reading): Long = readingDao.insertReading(reading)

    suspend fun updateReading(reading: Reading) = readingDao.updateReading(reading)

    suspend fun deleteReading(reading: Reading) = readingDao.deleteReading(reading)
}

