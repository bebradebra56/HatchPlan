package com.hatchi.planing.soft.data.dao

import androidx.room.*
import com.hatchi.planing.soft.data.entity.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE batchId = :batchId ORDER BY timestamp DESC")
    fun getNotesForBatch(batchId: Long): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("DELETE FROM notes WHERE batchId = :batchId")
    suspend fun deleteNotesForBatch(batchId: Long)
}

