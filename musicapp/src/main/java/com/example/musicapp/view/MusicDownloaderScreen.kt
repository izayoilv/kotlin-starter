package com.example.musicapp.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicapp.R
import com.example.musicapp.model.DownloadSnapshot
import com.example.musicapp.model.DownloadState
import com.example.musicapp.model.DownloadedTrack
import com.example.musicapp.model.PlaybackState
import com.example.musicapp.viewmodel.FormError
import com.example.musicapp.viewmodel.MusicDownloadViewModel

private const val DEFAULT_URL = "https://download.samplelib.com/mp3/sample-15s.mp3"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicDownloaderScreen(viewModel: MusicDownloadViewModel) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  var url by rememberSaveable { mutableStateOf(DEFAULT_URL) }
  var fileName by rememberSaveable { mutableStateOf("") }
  var wifiOnly by rememberSaveable { mutableStateOf(true) }

  Scaffold(
    topBar = {
      TopAppBar(title = { Text(stringResource(R.string.title)) })
    },
  ) { padding ->
    Column(
      modifier =
        Modifier
          .fillMaxSize()
          .padding(padding)
          .verticalScroll(rememberScrollState())
          .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      NotificationPermissionBanner()
      StoragePermissionBanner()

      OutlinedTextField(
        value = url,
        onValueChange = {
          url = it
          viewModel.clearFormError()
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.url_hint)) },
        singleLine = true,
        isError = uiState.formError != null,
        supportingText = {
          uiState.formError?.let { Text(stringResource(it.messageRes())) }
        },
      )

      OutlinedTextField(
        value = fileName,
        onValueChange = { fileName = it },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.file_name_hint)) },
        singleLine = true,
      )

      Row(verticalAlignment = Alignment.CenterVertically) {
        Switch(checked = wifiOnly, onCheckedChange = { wifiOnly = it })
        Spacer(Modifier.width(8.dp))
        Text(stringResource(R.string.wifi_only))
      }

      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Button(
          onClick = { viewModel.startDownload(url, fileName, wifiOnly) },
          enabled = !uiState.download.state.isActive,
          modifier = Modifier.weight(1f),
        ) {
          Text(stringResource(R.string.download))
        }
        if (uiState.download.state.isActive) {
          TextButton(onClick = { viewModel.cancelDownload() }) {
            Text(stringResource(R.string.cancel))
          }
        }
      }

      DownloadStatusCard(uiState.download)

      uiState.download.track?.let { track ->
        PlayerCard(
          track = track,
          playback = uiState.playback,
          onToggle = viewModel::togglePlayback,
          onSeek = viewModel::seekTo,
        )
      }
    }
  }
}

@Composable
private fun DownloadStatusCard(snapshot: DownloadSnapshot) {
  Card(modifier = Modifier.fillMaxWidth()) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Text(
        text = stringResource(R.string.status),
        style = MaterialTheme.typography.titleMedium,
      )
      when (snapshot.state) {
        DownloadState.IDLE -> Text(stringResource(R.string.idle))
        DownloadState.WAITING -> Text(stringResource(R.string.waiting))
        DownloadState.RUNNING -> {
          LinearProgressIndicator(
            progress = { snapshot.progress / 100f },
            modifier = Modifier.fillMaxWidth(),
          )
          Text("${snapshot.progress}%")
        }
        DownloadState.COMPLETED -> {
          snapshot.track?.let { track ->
            Text(track.name)
            Text(
              text = stringResource(R.string.saved_in_downloads),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
        DownloadState.FAILED ->
          Text(snapshot.error ?: stringResource(R.string.failed))
        DownloadState.CANCELLED -> Text(stringResource(R.string.cancelled))
      }
    }
  }
}

@Composable
private fun PlayerCard(
  track: DownloadedTrack,
  playback: PlaybackState,
  onToggle: () -> Unit,
  onSeek: (Long) -> Unit,
) {
  Card(modifier = Modifier.fillMaxWidth()) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Text(
        text = track.name,
        style = MaterialTheme.typography.titleMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Text(
        text = stringResource(R.string.saved_in_downloads),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )

      val duration = playback.durationMs.coerceAtLeast(0)
      val progress =
        if (duration > 0) {
          (playback.positionMs.toFloat() / duration).coerceIn(0f, 1f)
        } else {
          0f
        }

      Slider(
        value = progress,
        onValueChange = { value ->
          if (duration > 0) onSeek((value * duration).toLong())
        },
        enabled = duration > 0,
        modifier = Modifier.fillMaxWidth(),
      )

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        IconButton(onClick = onToggle, enabled = playback.isReady) {
          if (playback.isPlaying) {
            Icon(
              painter = painterResource(R.drawable.ic_pause),
              contentDescription = stringResource(R.string.pause),
            )
          } else {
            Icon(
              imageVector = Icons.Filled.PlayArrow,
              contentDescription = stringResource(R.string.play),
            )
          }
        }
        Text("${formatTime(playback.positionMs)} / ${formatTime(duration)}")
      }

      playback.error?.let { message ->
        Text(
          text = message,
          color = MaterialTheme.colorScheme.error,
          style = MaterialTheme.typography.bodySmall,
        )
      }
    }
  }
}

@Composable
private fun NotificationPermissionBanner() {
  if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

  val context = LocalContext.current
  var granted by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS,
      ) == PackageManager.PERMISSION_GRANTED,
    )
  }
  val launcher =
    rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted = it }

  if (!granted) {
    Card(modifier = Modifier.fillMaxWidth()) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Text(stringResource(R.string.notification_permission))
        TextButton(onClick = { launcher.launch(Manifest.permission.POST_NOTIFICATIONS) }) {
          Text(stringResource(R.string.allow))
        }
      }
    }
  }
}

@Composable
private fun StoragePermissionBanner() {
  if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) return

  val context = LocalContext.current
  var granted by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
      ) == PackageManager.PERMISSION_GRANTED,
    )
  }
  val launcher =
    rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted = it }

  if (!granted) {
    Card(modifier = Modifier.fillMaxWidth()) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Text(stringResource(R.string.storage_permission))
        TextButton(onClick = { launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE) }) {
          Text(stringResource(R.string.allow))
        }
      }
    }
  }
}

@StringRes
private fun FormError.messageRes(): Int =
  when (this) {
    FormError.BLANK_URL -> R.string.url_blank
    FormError.INVALID_URL -> R.string.url_invalid
  }

private val DownloadState.isActive: Boolean
  get() = this == DownloadState.WAITING || this == DownloadState.RUNNING

private fun formatTime(milliseconds: Long): String {
  val totalSeconds = (milliseconds / 1000).coerceAtLeast(0)
  val minutes = totalSeconds / 60
  val seconds = totalSeconds % 60
  return "%d:%02d".format(minutes, seconds)
}
