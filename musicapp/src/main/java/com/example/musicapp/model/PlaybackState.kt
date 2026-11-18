package com.example.musicapp.model

data class PlaybackState(
  val isPlaying: Boolean = false,
  val positionMs: Long = 0,
  val durationMs: Long = 0,
  val isReady: Boolean = false,
  val error: String? = null,
)
