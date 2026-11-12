package com.example.notesapp.viewmodel

import com.example.notesapp.model.Note
import com.example.notesapp.model.NotesError

data class NotesUiState(
  val notes: List<Note> = emptyList(),
  val isRefreshing: Boolean = false,
  val error: NotesError? = null,
)
