package com.example.notesapp.model

sealed interface NotesError {
  data object Network : NotesError

  data class Http(
    val code: Int,
    val message: String?,
  ) : NotesError

  data object Unknown : NotesError
}
