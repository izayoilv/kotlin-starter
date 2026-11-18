package com.example.musicapp.data

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.musicapp.model.DownloadContract
import com.example.musicapp.model.DownloadSnapshot
import com.example.musicapp.model.DownloadState
import com.example.musicapp.model.DownloadedTrack
import com.example.musicapp.worker.MusicDownloadWorker
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class WorkManagerMusicDownloadRepository(
  private val workManager: WorkManager,
) : MusicDownloadRepository {
  override val downloads: Flow<DownloadSnapshot> =
    workManager
      .getWorkInfosForUniqueWorkFlow(DownloadContract.WORK_NAME)
      .map { infos -> infos.firstOrNull()?.toSnapshot() ?: DownloadSnapshot() }
      .distinctUntilChanged()

  override fun enqueue(
    url: String,
    fileName: String?,
    wifiOnly: Boolean,
  ) {
    val constraints =
      Constraints
        .Builder()
        .setRequiredNetworkType(
          if (wifiOnly) NetworkType.UNMETERED else NetworkType.CONNECTED,
        ).build()

    val request =
      OneTimeWorkRequestBuilder<MusicDownloadWorker>()
        .setConstraints(constraints)
        .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
        .setInputData(
          workDataOf(
            DownloadContract.KEY_URL to url,
            DownloadContract.KEY_FILE_NAME to fileName,
          ),
        ).addTag(DownloadContract.WORK_NAME)
        .build()

    workManager.enqueueUniqueWork(
      DownloadContract.WORK_NAME,
      ExistingWorkPolicy.REPLACE,
      request,
    )
  }

  override fun cancel() {
    workManager.cancelUniqueWork(DownloadContract.WORK_NAME)
  }
}

private fun WorkInfo.toSnapshot(): DownloadSnapshot =
  when (state) {
    WorkInfo.State.ENQUEUED, WorkInfo.State.BLOCKED ->
      DownloadSnapshot(DownloadState.WAITING)

    WorkInfo.State.RUNNING ->
      DownloadSnapshot(
        state = DownloadState.RUNNING,
        progress = progress.getInt(DownloadContract.KEY_PROGRESS, 0),
      )

    WorkInfo.State.SUCCEEDED ->
      DownloadSnapshot(
        state = DownloadState.COMPLETED,
        progress = 100,
        track =
          DownloadedTrack(
            uri = outputData.getString(DownloadContract.KEY_URI).orEmpty(),
            name = outputData.getString(DownloadContract.KEY_NAME).orEmpty(),
            url = outputData.getString(DownloadContract.KEY_URL).orEmpty(),
            completedAt = outputData.getLong(DownloadContract.KEY_COMPLETED_AT, 0L),
          ).takeIf { it.uri.isNotBlank() },
      )

    WorkInfo.State.FAILED ->
      DownloadSnapshot(
        state = DownloadState.FAILED,
        error = outputData.getString(DownloadContract.KEY_ERROR),
      )

    WorkInfo.State.CANCELLED -> DownloadSnapshot(DownloadState.CANCELLED)
  }
