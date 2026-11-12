package com.example.notesapp.viewmodel

import com.example.notesapp.model.Note
import com.example.notesapp.model.NotesError

data class NoteDetailUiState(
  val note: Note? = null,
  val isLoading: Boolean = true,
  val isDeleting: Boolean = false,
  val error: NotesError? = null,
)
