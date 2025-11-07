package com.hatchi.planing.soft.data.repository

import com.hatchi.planing.soft.data.dao.NoteDao
import com.hatchi.planing.soft.data.entity.Note
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    fun getNotesForBatch(batchId: Long): Flow<List<Note>> =
        noteDao.getNotesForBatch(batchId)

    suspend fun getNoteById(id: Long): Note? = noteDao.getNoteById(id)

    suspend fun insertNote(note: Note): Long = noteDao.insertNote(note)

    suspend fun updateNote(note: Note) = noteDao.updateNote(note)

    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)
}

