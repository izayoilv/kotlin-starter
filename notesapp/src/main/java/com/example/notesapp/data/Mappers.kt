package com.example.notesapp.data

import com.example.notesapp.data.local.NoteEntity
import com.example.notesapp.data.remote.NoteDto
import com.example.notesapp.model.Note

fun NoteEntity.toDomain(): Note =
  Note(
    id = id,
    title = title,
    body = body,
    createdAt = createdAt,
  )

fun NoteDto.toEntity(): NoteEntity =
  NoteEntity(
    id = id,
    title = title,
    body = body,
    createdAt = createdAt,
  )
