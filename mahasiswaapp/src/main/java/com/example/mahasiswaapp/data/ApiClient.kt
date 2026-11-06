package com.example.mahasiswaapp.data

import com.example.mahasiswaapp.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
  private val httpClient =
    OkHttpClient
      .Builder()
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .build()

  val api: ApiService by lazy {
    Retrofit
      .Builder()
      .baseUrl(BuildConfig.API_BASE_URL)
      .client(httpClient)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(ApiService::class.java)
  }
}
