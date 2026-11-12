package com.example.notesapp.data

import com.example.notesapp.data.local.NoteDao
import com.example.notesapp.data.local.NoteEntity
import com.example.notesapp.data.remote.CreateNoteRequest
import com.example.notesapp.data.remote.NoteDto
import com.example.notesapp.data.remote.NotesApi
import com.example.notesapp.data.remote.NotesApiException
import com.example.notesapp.model.Note
import com.example.notesapp.model.NotesError
import com.example.notesapp.model.NotesRepository
import com.example.notesapp.model.NotesResult
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotesRepositoryImpl(
  private val api: NotesApi,
  private val dao: NoteDao,
) : NotesRepository {
  override val notes: Flow<List<Note>> =
    dao.observeAll().map { entities -> entities.map(NoteEntity::toDomain) }

  override fun observeNote(id: Long): Flow<Note?> = dao.observeById(id).map { it?.toDomain() }

  override suspend fun refresh(): NotesResult<Unit> =
    runNotes {
      val remote = api.getNotes()
      dao.sync(remote.map(NoteDto::toEntity))
    }

  override suspend fun add(title: String, body: String): NotesResult<Note> =
    runNotes {
      val created = api.createNote(CreateNoteRequest(title.trim(), body))
      val entity = created.toEntity()
      dao.upsert(entity)
      entity.toDomain()
    }

  override suspend fun delete(id: Long): NotesResult<Unit> =
    runNotes {
      try {
        api.deleteNote(id)
      } catch (e: NotesApiException) {
        if (e.code != 404) throw e
      }
      dao.deleteById(id)
    }

  private suspend fun <T> runNotes(block: suspend () -> T): NotesResult<T> =
    try {
      NotesResult.Success(block())
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      NotesResult.Failure(e.toNotesError())
    }

  private fun Throwable.toNotesError(): NotesError =
    when (this) {
      is NotesApiException -> NotesError.Http(code, message)
      is IOException -> NotesError.Network
      else -> NotesError.Unknown
    }
}
