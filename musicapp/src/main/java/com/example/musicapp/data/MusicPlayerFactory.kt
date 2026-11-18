package com.example.musicapp.data

import android.content.Context

class MusicPlayerFactory(
  private val context: Context,
) {
  fun create(): MusicPlayer = MusicPlayer(context.applicationContext)
}
