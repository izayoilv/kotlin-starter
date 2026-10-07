package com.example.notesapp

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class NoteStorageTest {
  @get:Rule
  val temp = TemporaryFolder()

  private fun storage() = NoteStorage(temp.root)

  private fun note(id: Long, title: String = "Judul", createdAt: Long = id) =
    Note(id = id, title = title, body = "Isi $id", author = "Ani", createdAt = createdAt)

  @Test
  fun `save then list returns the note`() {
    val storage = storage()
    storage.save(note(1))
    val notes = storage.listNotes()
    assertEquals(1, notes.size)
    assertEquals("Judul", notes.first().title)
    assertEquals("Ani", notes.first().author)
  }

  @Test
  fun `list is sorted newest first`() {
    val storage = storage()
    storage.save(note(1, title = "Lama", createdAt = 100))
    storage.save(note(2, title = "Baru", createdAt = 200))
    assertEquals(listOf("Baru", "Lama"), storage.listNotes().map { it.title })
  }

  @Test
  fun `load returns saved note and null for unknown`() {
    val storage = storage()
    storage.save(note(7))
    assertEquals(7L, storage.load(7)?.id)
    assertNull(storage.load(999))
  }

  @Test
  fun `delete removes only that note`() {
    val storage = storage()
    storage.save(note(1))
    storage.save(note(2))
    assertTrue(storage.delete(1))
    assertEquals(listOf(2L), storage.listNotes().map { it.id })
  }

  @Test
  fun `corrupt file is skipped`() {
    val storage = storage()
    storage.save(note(1))
    File(temp.root, "note_2.json").writeText("{ not json")
    assertEquals(listOf(1L), storage.listNotes().map { it.id })
  }

  @Test
  fun `exportJson round-trips the notes`() {
    val storage = storage()
    storage.save(note(1, title = "A", createdAt = 100))
    storage.save(note(2, title = "B", createdAt = 200))
    val restored = Gson().fromJson(storage.exportJson(), Array<Note>::class.java).toList()
    assertEquals(listOf("B", "A"), restored.map { it.title })
  }
}
