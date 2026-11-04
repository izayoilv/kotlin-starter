package com.example.mahasiswaapp

import android.app.Application

class MahasiswaApplication : Application() {
  val container: AppContainer by lazy { AppContainer() }
}
