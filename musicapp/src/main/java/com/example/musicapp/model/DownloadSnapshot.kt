package com.example.musicapp.model

enum class DownloadState {
  IDLE,
  WAITING,
  RUNNING,
  COMPLETED,
  FAILED,
  CANCELLED,
}

data class DownloadSnapshot(
  val state: DownloadState = DownloadState.IDLE,
  val progress: Int = 0,
  val track: DownloadedTrack? = null,
  val error: String? = null,
)
