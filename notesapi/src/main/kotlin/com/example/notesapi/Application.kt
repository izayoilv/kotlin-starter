package com.example.notesapi

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.CancellationException

fun main() {
  embeddedServer(
    Netty,
    port = System.getenv("PORT")?.toIntOrNull() ?: 8081,
    host = "0.0.0.0",
    module = Application::module,
  ).start(wait = true)
}

fun Application.module() {
  configure(NotesRepository(DatabaseFactory.connect()))
}

fun Application.configure(repository: NotesRepository) {
  install(ContentNegotiation) {
    json()
  }
  install(CallLogging)
  install(StatusPages) {
    exception<ContentTransformationException> { call, _ ->
      call.respond(HttpStatusCode.BadRequest, ApiError("Permintaan tidak valid"))
    }
    exception<BadRequestException> { call, _ ->
      call.respond(HttpStatusCode.BadRequest, ApiError("Permintaan tidak valid"))
    }
    exception<Exception> { call, cause ->
      if (cause is CancellationException) throw cause
      this@configure.environment.log.error("Unhandled error", cause)
      call.respond(HttpStatusCode.InternalServerError, ApiError("Terjadi kesalahan server"))
    }
  }
  routing {
    get("/health") {
      call.respondText("""{"status":"ok"}""", ContentType.Application.Json)
    }
    notesRoutes(repository)
  }
}
