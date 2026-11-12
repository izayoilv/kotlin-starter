package com.example.notesapp.model

import kotlinx.coroutines.flow.Flow

interface NotesRepository {
  val notes: Flow<List<Note>>

  fun observeNote(id: Long): Flow<Note?>

  suspend fun refresh(): NotesResult<Unit>

  suspend fun add(title: String, body: String): NotesResult<Note>

  suspend fun delete(id: Long): NotesResult<Unit>
}
