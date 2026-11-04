package com.example.mahasiswaapp

import com.example.mahasiswaapp.data.ApiClient
import com.example.mahasiswaapp.data.MahasiswaRepository
import com.example.mahasiswaapp.data.NetworkMahasiswaRepository

class AppContainer {
  val repository: MahasiswaRepository by lazy {
    NetworkMahasiswaRepository(ApiClient.api)
  }
}
