package com.example.mahasiswaapi

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.gson.gson
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import kotlinx.coroutines.CancellationException

fun main() {
  embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
    .start(wait = true)
}

fun Application.module() {
  install(ContentNegotiation) {
    gson()
  }

  routing {
    get("/") {
      call.respondText("Mahasiswa API is running")
    }

    get("/read.php") {
      call.respond(ListResponse("success", Database.all()))
    }

    post("/create.php") {
      val mahasiswa = call.receiveOrNull<Mahasiswa>() ?: return@post
      call.respondResult(Database.create(mahasiswa))
    }

    put("/update.php") {
      val mahasiswa = call.receiveOrNull<Mahasiswa>() ?: return@put
      call.respondResult(Database.update(mahasiswa))
    }

    delete("/delete.php") {
      val mahasiswa = call.receiveOrNull<Mahasiswa>() ?: return@delete
      call.respondResult(Database.delete(mahasiswa.id))
    }
  }
}

private suspend fun ApplicationCall.respondResult(result: DbResult) {
  when (result) {
    is DbResult.Success -> respond(MessageResponse("success", result.message))
    is DbResult.Error -> respond(MessageResponse("error", result.message))
  }
}

private suspend inline fun <reified T : Any> ApplicationCall.receiveOrNull(): T? =
  try {
    receive<T>()
  } catch (e: CancellationException) {
    throw e
  } catch (_: Exception) {
    respond(HttpStatusCode.BadRequest, MessageResponse("error", "Permintaan tidak valid"))
    null
  }
