package com.example.notesapi

import java.io.File
import java.nio.file.Files
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.Assert.assertEquals
import org.junit.Test

class DatabaseFactoryTest {
  @Test
  fun `connections use the configured busy timeout`() {
    val file = Files.createTempFile("notes-test-", ".db").toFile()
    try {
      val database = DatabaseFactory.connect(file.absolutePath)

      val timeout =
        transaction(database) {
          exec("PRAGMA busy_timeout") { result ->
            if (result.next()) result.getInt(1) else -1
          }
        }

      assertEquals(5000, timeout)
    } finally {
      listOf(file, File("${file.absolutePath}-wal"), File("${file.absolutePath}-shm"))
        .forEach(File::delete)
    }
  }
}
