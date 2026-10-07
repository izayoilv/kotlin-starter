package com.example.notesapp

import com.google.gson.Gson
import java.io.File

class NoteStorage(private val dir: File) {
  private val gson = Gson()

  fun listNotes(): List<Note> {
    val files = dir.listFiles() ?: return emptyList()
    return files
      .filter { it.name.endsWith(".json") }
      .mapNotNull { readNote(it) }
      .sortedByDescending { it.createdAt }
  }

  fun load(id: Long): Note? = readNote(File(dir, fileName(id)))

  fun save(note: Note) {
    dir.mkdirs()
    File(dir, fileName(note.id)).writeText(gson.toJson(note))
  }

  fun delete(id: Long): Boolean = File(dir, fileName(id)).delete()

  fun exportJson(): String = gson.toJson(listNotes())

  private fun readNote(file: File): Note? =
    try {
      gson.fromJson(file.readText(), Note::class.java)
    } catch (e: Exception) {
      null
    }

  private fun fileName(id: Long) = "note_$id.json"
}
