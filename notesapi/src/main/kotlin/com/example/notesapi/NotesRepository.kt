package com.example.notesapi

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

class NotesRepository(
  private val database: Database,
) {
  suspend fun all(): List<NoteDto> =
    withContext(Dispatchers.IO) {
      suspendTransaction(database) {
        NotesTable
          .selectAll()
          .orderBy(NotesTable.createdAt to SortOrder.DESC)
          .map { row ->
            NoteDto(
              id = row[NotesTable.id].value,
              title = row[NotesTable.title],
              body = row[NotesTable.body],
              createdAt = row[NotesTable.createdAt],
            )
          }
      }
    }

  suspend fun create(request: CreateNoteRequest): NoteDto =
    withContext(Dispatchers.IO) {
      suspendTransaction(database) {
        val now = System.currentTimeMillis()
        val id =
          NotesTable.insertAndGetId {
            it[title] = request.title
            it[body] = request.body
            it[createdAt] = now
          }
        NoteDto(
          id = id.value,
          title = request.title,
          body = request.body,
          createdAt = now,
        )
      }
    }

  suspend fun delete(id: Long): Boolean =
    withContext(Dispatchers.IO) {
      suspendTransaction(database) {
        NotesTable.deleteWhere { NotesTable.id eq EntityID(id, NotesTable) } > 0
      }
    }
}
