package com.example.mahasiswaapp.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {
  @GET("read.php")
  suspend fun getAll(): ApiListResponse

  @POST("create.php")
  suspend fun create(
    @Body mahasiswa: Mahasiswa,
  ): ApiMessageResponse

  @PUT("update.php")
  suspend fun update(
    @Body mahasiswa: Mahasiswa,
  ): ApiMessageResponse

  @HTTP(method = "DELETE", path = "delete.php", hasBody = true)
  suspend fun delete(
    @Body mahasiswa: Mahasiswa,
  ): ApiMessageResponse
}
