package com.example.musicapp.data

import com.example.musicapp.model.DownloadSnapshot
import kotlinx.coroutines.flow.Flow

interface MusicDownloadRepository {
  val downloads: Flow<DownloadSnapshot>

  fun enqueue(
    url: String,
    fileName: String?,
    wifiOnly: Boolean,
  )

  fun cancel()
}
