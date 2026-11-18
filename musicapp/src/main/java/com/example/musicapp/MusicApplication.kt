package com.example.musicapp

import android.app.Application
import androidx.work.Configuration
import com.example.musicapp.di.AppGraph
import dev.zacsweers.metro.createGraphFactory

class MusicApplication : Application(), Configuration.Provider {
  lateinit var graph: AppGraph
    private set

  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder().setWorkerFactory(graph.workerFactory).build()

  override fun onCreate() {
    super.onCreate()
    graph = createGraphFactory<AppGraph.Factory>().create(this)
  }
}
