package com.example.notesapp.model

sealed interface NotesResult<out T> {
  data class Success<T>(
    val value: T,
  ) : NotesResult<T>

  data class Failure(
    val error: NotesError,
  ) : NotesResult<Nothing>
}
