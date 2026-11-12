package com.example.notesapp.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

interface NotesApi {
  suspend fun getNotes(): List<NoteDto>

  suspend fun createNote(request: CreateNoteRequest): NoteDto

  suspend fun deleteNote(id: Long)
}

class NotesApiException(
  val code: Int,
  override val message: String?,
) : Exception(message)

class KtorNotesApi(
  private val client: HttpClient,
  private val baseUrl: String,
) : NotesApi {
  override suspend fun getNotes(): List<NoteDto> = execute { client.get("$baseUrl/api/notes").body() }

  override suspend fun createNote(request: CreateNoteRequest): NoteDto =
    execute {
      client
        .post("$baseUrl/api/notes") {
          contentType(ContentType.Application.Json)
          setBody(request)
        }.body()
    }

  override suspend fun deleteNote(id: Long) {
    execute { client.delete("$baseUrl/api/notes/$id") }
  }

  private suspend fun <T> execute(block: suspend () -> T): T =
    try {
      block()
    } catch (e: ClientRequestException) {
      throw NotesApiException(e.response.status.value, e.response.bodyAsText())
    } catch (e: ServerResponseException) {
      throw NotesApiException(e.response.status.value, e.response.bodyAsText())
    }
}

object NotesHttpClient {
  fun create(): HttpClient =
    HttpClient(OkHttp) {
      expectSuccess = true
      install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
      }
      install(HttpTimeout) {
        requestTimeoutMillis = 10_000
        connectTimeoutMillis = 10_000
        socketTimeoutMillis = 10_000
      }
    }
}
