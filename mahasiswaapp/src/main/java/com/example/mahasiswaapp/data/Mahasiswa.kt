package com.example.mahasiswaapp.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Mahasiswa(
  @SerializedName("id") val id: Int = 0,
  @SerializedName("nim") val nim: String,
  @SerializedName("nama") val nama: String,
  @SerializedName("jurusan") val jurusan: String,
  @SerializedName("email") val email: String,
) : Serializable
