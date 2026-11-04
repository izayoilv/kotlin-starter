package com.example.mahasiswaapi

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
      val mahasiswa = call.receive<Mahasiswa>()
      call.respondResult(Database.create(mahasiswa))
    }

    put("/update.php") {
      val mahasiswa = call.receive<Mahasiswa>()
      call.respondResult(Database.update(mahasiswa))
    }

    delete("/delete.php") {
      val mahasiswa = call.receive<Mahasiswa>()
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
