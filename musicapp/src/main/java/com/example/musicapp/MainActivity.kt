package com.example.musicapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.musicapp.di.AppGraph
import com.example.musicapp.view.MusicDownloaderScreen
import com.example.musicapp.view.theme.MusicTheme
import com.example.musicapp.viewmodel.MusicDownloadViewModel

class MainActivity : ComponentActivity() {
  private lateinit var graph: AppGraph
  private val viewModel: MusicDownloadViewModel by viewModels { graph.metroViewModelFactory }

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
    graph = (application as MusicApplication).graph
    setContent {
      MusicTheme {
        MusicDownloaderScreen(viewModel)
      }
    }
  }

  override fun onStop() {
    super.onStop()
    if (!isChangingConfigurations) {
      viewModel.pausePlayback()
    }
  }
}
