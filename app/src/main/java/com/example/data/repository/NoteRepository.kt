package com.example.data.repository

import com.example.data.local.NoteDao
import com.example.data.local.NotebookDao
import com.example.data.local.toEntity
import com.example.data.local.toNote
import com.example.data.local.toNotebook
import com.example.data.model.Note
import com.example.data.model.Notebook
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepository(
    private val noteDao: NoteDao,
    private val notebookDao: NotebookDao
) {
    val allNotes: Flow<List<Note>> = noteDao.getAllNotes().map { entities ->
        entities.map { it.toNote() }
    }

    val allNotebooks: Flow<List<Notebook>> = notebookDao.getAllNotebooks().map { entities ->
        entities.map { it.toNotebook() }
    }

    fun getNote(id: Long): Flow<Note?> = noteDao.getNoteById(id).map { it?.toNote() }

    suspend fun saveNote(note: Note): Long {
        return if (note.id == 0L) {
            noteDao.insertNote(note.toEntity())
        } else {
            noteDao.updateNote(note.toEntity())
            note.id
        }
    }

    suspend fun deleteNote(id: Long) {
        noteDao.deleteNoteById(id)
    }

    suspend fun createNotebook(name: String, subtitle: String): Long {
        val nextIndex = notebookDao.getMaxOrderIndex() + 1
        val entity = Notebook(
            orderIndex = nextIndex,
            name = name.trim(),
            subtitle = subtitle.trim().ifEmpty { "Curated Collection" }
        ).toEntity()
        return notebookDao.insertNotebook(entity)
    }

    suspend fun deleteNotebook(notebook: Notebook, fallbackNotebook: String) {
        noteDao.reassignNotebook(notebook.name, fallbackNotebook)
        notebookDao.deleteNotebookById(notebook.id)
    }
}
