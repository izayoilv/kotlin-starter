package com.example.notesapi

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object NotesTable : LongIdTable("notes") {
  val title = text("title")
  val body = text("body")
  val createdAt = long("created_at")
}
