package com.example.notesapi

import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object DatabaseFactory {
  fun connect(path: String = System.getenv("NOTES_DB") ?: "notes.db"): Database {
    return Database.connect(
      url = "jdbc:sqlite:$path",
      driver = "org.sqlite.JDBC",
      setupConnection = { connection ->
        connection.createStatement().use {
          it.execute("PRAGMA journal_mode=WAL")
          it.execute("PRAGMA busy_timeout=5000")
        }
      },
    ).also { db ->
      transaction(db) { SchemaUtils.create(NotesTable) }
    }
  }
}
