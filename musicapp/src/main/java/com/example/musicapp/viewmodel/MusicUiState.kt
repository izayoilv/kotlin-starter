package com.example.musicapp.viewmodel

import com.example.musicapp.model.DownloadSnapshot
import com.example.musicapp.model.PlaybackState

data class MusicUiState(
  val download: DownloadSnapshot = DownloadSnapshot(),
  val playback: PlaybackState = PlaybackState(),
  val formError: FormError? = null,
)
