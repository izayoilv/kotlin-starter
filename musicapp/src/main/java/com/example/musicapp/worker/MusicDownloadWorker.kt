package com.example.musicapp.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.musicapp.data.DownloadNotifications
import com.example.musicapp.data.HttpStatusException
import com.example.musicapp.data.MusicDownloader
import com.example.musicapp.model.DownloadContract
import com.example.musicapp.model.RetryPolicy
import com.example.musicapp.model.UrlValidator
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import java.io.IOException
import kotlinx.coroutines.CancellationException

@AssistedInject
class MusicDownloadWorker(
  context: Context,
  @Assisted params: WorkerParameters,
  private val downloader: MusicDownloader,
  private val notifications: DownloadNotifications,
) : CoroutineWorker(context, params) {
  override suspend fun doWork(): Result {
    val url = inputData.getString(DownloadContract.KEY_URL)
    if (url == null || !UrlValidator.isValid(url)) {
      return fail("URL tidak valid")
    }

    return try {
      val track =
        downloader.download(url, inputData.getString(DownloadContract.KEY_FILE_NAME)) { progress ->
          setProgress(workDataOf(DownloadContract.KEY_PROGRESS to progress))
        }
      notifications.notifySuccess(track)
      Result.success(
        workDataOf(
          DownloadContract.KEY_URI to track.uri,
          DownloadContract.KEY_NAME to track.name,
          DownloadContract.KEY_URL to track.url,
          DownloadContract.KEY_COMPLETED_AT to track.completedAt,
        ),
      )
    } catch (e: CancellationException) {
      throw e
    } catch (e: HttpStatusException) {
      if (e.code in 500..599 && RetryPolicy.shouldRetry(runAttemptCount)) {
        Result.retry()
      } else {
        fail(e.message ?: "HTTP ${e.code}")
      }
    } catch (e: IOException) {
      if (RetryPolicy.shouldRetry(runAttemptCount)) {
        Result.retry()
      } else {
        fail(e.message ?: "Koneksi gagal")
      }
    } catch (e: Exception) {
      fail(e.message ?: "Unduhan gagal")
    }
  }

  private fun fail(message: String): Result {
    notifications.notifyFailure(message)
    return Result.failure(workDataOf(DownloadContract.KEY_ERROR to message))
  }

  @WorkerKey(MusicDownloadWorker::class)
  @ContributesIntoMap(
    AppScope::class,
    binding = binding<MetroWorkerFactory.WorkerInstanceFactory<*>>(),
  )
  @AssistedFactory
  abstract class Factory : MetroWorkerFactory.WorkerInstanceFactory<MusicDownloadWorker>
}
