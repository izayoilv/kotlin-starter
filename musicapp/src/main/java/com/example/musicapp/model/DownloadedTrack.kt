package com.example.musicapp.model

data class DownloadedTrack(
  val uri: String,
  val name: String,
  val url: String,
  val completedAt: Long,
)
