package com.example.musicapp.data

import android.content.Context
import android.net.Uri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicapp.model.PlaybackState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MusicPlayer(
  context: Context,
) {
  private val exoPlayer =
    ExoPlayer.Builder(context).build().apply {
      setAudioAttributes(
        AudioAttributes
          .Builder()
          .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
          .setUsage(C.USAGE_MEDIA)
          .build(),
        true,
      )
      addListener(
        object : Player.Listener {
          override fun onIsPlayingChanged(isPlaying: Boolean) {
            updateState()
          }

          override fun onPlaybackStateChanged(playbackState: Int) {
            updateState()
          }

          override fun onPlayerError(error: PlaybackException) {
            updateState(error.message)
          }
        },
      )
    }

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
  private val _state = MutableStateFlow(PlaybackState())
  val state: StateFlow<PlaybackState> = _state.asStateFlow()
  private var ticker: Job? = null

  fun load(uri: String) {
    exoPlayer.stop()
    exoPlayer.clearMediaItems()
    _state.value = PlaybackState()
    exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(uri)))
    exoPlayer.prepare()
    exoPlayer.playWhenReady = false
    startTicker()
    updateState()
  }

  fun togglePlayPause() {
    if (exoPlayer.isPlaying) {
      exoPlayer.pause()
    } else {
      exoPlayer.play()
    }
    updateState()
  }

  fun play() {
    exoPlayer.play()
    updateState()
  }

  fun pause() {
    exoPlayer.pause()
    updateState()
  }

  fun seekTo(positionMs: Long) {
    exoPlayer.seekTo(positionMs.coerceAtLeast(0))
    updateState()
  }

  fun stopAndClear() {
    ticker?.cancel()
    ticker = null
    exoPlayer.stop()
    exoPlayer.clearMediaItems()
    _state.value = PlaybackState()
  }

  fun release() {
    ticker?.cancel()
    scope.cancel()
    exoPlayer.release()
  }

  private fun startTicker() {
    ticker?.cancel()
    ticker =
      scope.launch {
        while (isActive) {
          updateState()
          delay(500)
        }
      }
  }

  private fun updateState(error: String? = null) {
    val duration =
      exoPlayer.duration.takeIf { it != C.TIME_UNSET && it > 0 } ?: 0L
    _state.value =
      PlaybackState(
        isPlaying = exoPlayer.isPlaying,
        positionMs = exoPlayer.currentPosition.coerceAtLeast(0),
        durationMs = duration,
        isReady = exoPlayer.playbackState == Player.STATE_READY,
        error = error ?: _state.value.error,
      )
  }
}
