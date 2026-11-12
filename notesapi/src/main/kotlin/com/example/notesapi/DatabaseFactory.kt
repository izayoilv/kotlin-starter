package com.example.notesapi

import java.sql.DriverManager
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object DatabaseFactory {
  fun connect(path: String = System.getenv("NOTES_DB") ?: "notes.db"): Database {
    DriverManager.getConnection("jdbc:sqlite:$path").use { conn ->
      conn.createStatement().use {
        it.execute("PRAGMA journal_mode=WAL")
        it.execute("PRAGMA busy_timeout=5000")
      }
    }
    return Database.connect("jdbc:sqlite:$path", "org.sqlite.JDBC").also { db ->
      transaction(db) { SchemaUtils.create(NotesTable) }
    }
  }
}
