package com.example.mahasiswaapp.data

import kotlinx.coroutines.CancellationException

interface MahasiswaRepository {
  suspend fun getAll(): Result<List<Mahasiswa>>
  suspend fun create(mahasiswa: Mahasiswa): Result<String>
  suspend fun update(mahasiswa: Mahasiswa): Result<String>
  suspend fun delete(mahasiswa: Mahasiswa): Result<String>
}

class NetworkMahasiswaRepository(
  private val api: ApiService,
) : MahasiswaRepository {
  override suspend fun getAll(): Result<List<Mahasiswa>> = safeCall {
    val response = api.getAll()
    if (response.status == "success" && response.data != null) {
      response.data
    } else {
      throw ApiException(response.message ?: "Gagal memuat data")
    }
  }

  override suspend fun create(mahasiswa: Mahasiswa): Result<String> = safeCall {
    api.create(mahasiswa).requireSuccess()
  }

  override suspend fun update(mahasiswa: Mahasiswa): Result<String> = safeCall {
    api.update(mahasiswa).requireSuccess()
  }

  override suspend fun delete(mahasiswa: Mahasiswa): Result<String> = safeCall {
    api.delete(mahasiswa).requireSuccess()
  }

  private fun ApiMessageResponse.requireSuccess(): String {
    if (status != "success") throw ApiException(message ?: "Gagal memproses data")
    return message ?: "Berhasil"
  }

  private suspend fun <T> safeCall(block: suspend () -> T): Result<T> =
    try {
      Result.success(block())
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Result.failure(e)
    }
}
