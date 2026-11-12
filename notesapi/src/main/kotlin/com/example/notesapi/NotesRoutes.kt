package com.example.notesapi

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.notesRoutes(repository: NotesRepository) {
  get("/api/notes") {
    call.respond(repository.all())
  }

  post("/api/notes") {
    val request = call.receive<CreateNoteRequest>()
    val title = request.title.trim()
    if (title.isBlank()) {
      call.respond(HttpStatusCode.BadRequest, ApiError("Judul tidak boleh kosong"))
      return@post
    }
    call.respond(HttpStatusCode.Created, repository.create(request.copy(title = title)))
  }

  delete("/api/notes/{id}") {
    val id = call.parameters["id"]?.toLongOrNull()
    if (id == null) {
      call.respond(HttpStatusCode.BadRequest, ApiError("ID tidak valid"))
      return@delete
    }
    if (repository.delete(id)) {
      call.respond(HttpStatusCode.NoContent)
    } else {
      call.respond(HttpStatusCode.NotFound, ApiError("Catatan tidak ditemukan"))
    }
  }
}
