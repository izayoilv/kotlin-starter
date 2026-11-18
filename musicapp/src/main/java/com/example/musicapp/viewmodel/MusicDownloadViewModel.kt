package com.example.musicapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.MusicDownloadRepository
import com.example.musicapp.data.MusicPlayerFactory
import com.example.musicapp.model.DownloadState
import com.example.musicapp.model.UrlValidator
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@ContributesIntoMap(AppScope::class)
@ViewModelKey
@Inject
class MusicDownloadViewModel(
  private val repository: MusicDownloadRepository,
  playerFactory: MusicPlayerFactory,
) : ViewModel() {
  private val player = playerFactory.create()
  private val formError = MutableStateFlow<FormError?>(null)
  private var lastLoadedUri: String? = null

  val uiState: StateFlow<MusicUiState> =
    combine(repository.downloads, player.state, formError) { download, playback, error ->
      MusicUiState(download, playback, error)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MusicUiState())

  init {
    viewModelScope.launch {
      repository.downloads.collect { snapshot ->
        val track = snapshot.track
        if (snapshot.state == DownloadState.COMPLETED &&
          track != null &&
          track.uri != lastLoadedUri
        ) {
          lastLoadedUri = track.uri
          player.load(track.uri)
        }
      }
    }
  }

  fun startDownload(
    url: String,
    fileName: String,
    wifiOnly: Boolean,
  ) {
    val trimmed = url.trim()
    when {
      trimmed.isBlank() -> formError.value = FormError.BLANK_URL
      !UrlValidator.isValid(trimmed) -> formError.value = FormError.INVALID_URL
      else -> {
        formError.value = null
        player.stopAndClear()
        lastLoadedUri = null
        repository.enqueue(trimmed, fileName.trim().ifBlank { null }, wifiOnly)
      }
    }
  }

  fun cancelDownload() = repository.cancel()

  fun togglePlayback() = player.togglePlayPause()

  fun seekTo(positionMs: Long) = player.seekTo(positionMs)

  fun pausePlayback() = player.pause()

  fun clearFormError() {
    formError.value = null
  }

  override fun onCleared() {
    player.release()
  }
}
