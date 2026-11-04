package com.example.mahasiswaapp.data

data class ApiListResponse(
  val status: String,
  val message: String? = null,
  val data: List<Mahasiswa>? = null,
)

data class ApiMessageResponse(
  val status: String,
  val message: String? = null,
)

class ApiException(message: String) : Exception(message)
