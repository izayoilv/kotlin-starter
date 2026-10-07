package com.example.notesapp

data class Note(
  val id: Long,
  val title: String,
  val body: String,
  val author: String,
  val createdAt: Long,
)
