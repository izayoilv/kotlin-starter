package com.example.mahasiswaapi

data class Mahasiswa(
  val id: Int = 0,
  val nim: String,
  val nama: String,
  val jurusan: String,
  val email: String,
)

data class ListResponse(val status: String, val data: List<Mahasiswa>)

data class MessageResponse(val status: String, val message: String)

sealed interface DbResult {
  data class Success(val message: String) : DbResult
  data class Error(val message: String) : DbResult
}
