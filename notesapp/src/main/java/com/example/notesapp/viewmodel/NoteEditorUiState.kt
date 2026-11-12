package com.example.notesapp.viewmodel

import com.example.notesapp.model.NotesError

data class NoteEditorUiState(
  val titleError: Boolean = false,
  val isSaving: Boolean = false,
  val error: NotesError? = null,
)
