package com.example.notesapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
  @PrimaryKey val id: Long,
  val title: String,
  val body: String,
  val createdAt: Long,
)
