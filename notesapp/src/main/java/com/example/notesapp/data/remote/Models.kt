package com.example.notesapp.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class NoteDto(
  val id: Long,
  val title: String,
  val body: String,
  val createdAt: Long,
)

@Serializable
data class CreateNoteRequest(
  val title: String,
  val body: String,
)

@Serializable
data class ApiError(
  val message: String,
)
