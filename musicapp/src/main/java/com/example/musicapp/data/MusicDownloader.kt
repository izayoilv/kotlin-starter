package com.example.musicapp.data

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import com.example.musicapp.model.DownloadedTrack
import com.example.musicapp.model.FileName
import io.ktor.client.HttpClient
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.utils.io.readAvailable
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class HttpStatusException(
  val code: Int,
) : IOException("HTTP $code")

class MusicDownloader(
  private val context: Context,
  private val client: HttpClient,
) {
  suspend fun download(
    url: String,
    fileName: String?,
    onProgress: suspend (Int) -> Unit,
  ): DownloadedTrack {
    val displayName =
      FileName.withExtension(
        fileName ?: "music_${System.currentTimeMillis()}",
        url,
      )
    val mimeType = FileName.mimeType(displayName.substringAfterLast('.', "mp3"))
    val target = createTarget(displayName, mimeType)
    var downloaded = 0L

    try {
      client.prepareGet(url).execute { response ->
        if (!response.status.isSuccess()) {
          throw HttpStatusException(response.status.value)
        }

        val total = response.headers[HttpHeaders.ContentLength]?.toLongOrNull() ?: -1L
        val channel = response.bodyAsChannel()
        target.open().use { output ->
          val buffer = ByteArray(64 * 1024)
          while (true) {
            val read = channel.readAvailable(buffer)
            if (read == -1) break
            if (read == 0) continue
            output.write(buffer, 0, read)
            downloaded += read
            if (total > 0) {
              onProgress(((downloaded * 100) / total).toInt().coerceIn(0, 100))
            }
          }
        }
      }

      if (downloaded == 0L) {
        throw IOException("File kosong")
      }
      target.complete()
      onProgress(100)
    } catch (e: Exception) {
      target.abort()
      throw e
    }

    return DownloadedTrack(
      uri = target.uri.toString(),
      name = displayName,
      url = url,
      completedAt = System.currentTimeMillis(),
    )
  }

  private fun createTarget(
    displayName: String,
    mimeType: String,
  ): DownloadTarget =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      createMediaStoreTarget(displayName, mimeType)
    } else {
      createLegacyTarget(displayName)
    }

  private fun createMediaStoreTarget(
    displayName: String,
    mimeType: String,
  ): DownloadTarget {
    val resolver = context.contentResolver
    val values =
      ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        put(MediaStore.MediaColumns.IS_PENDING, 1)
      }
    val uri =
      resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
        ?: throw IOException("Gagal membuat entri Downloads")

    return DownloadTarget(
      uri = uri,
      open = {
        resolver.openOutputStream(uri)
          ?: throw IOException("Gagal membuka Downloads")
      },
      complete = {
        val done =
          ContentValues().apply {
            put(MediaStore.MediaColumns.IS_PENDING, 0)
          }
        resolver.update(uri, done, null, null)
      },
      abort = {
        resolver.delete(uri, null, null)
      },
    )
  }

  @Suppress("DEPRECATION")
  private fun createLegacyTarget(displayName: String): DownloadTarget {
    if (ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      throw IOException("Izin penyimpanan diperlukan")
    }

    val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    if (!directory.exists() && !directory.mkdirs()) {
      throw IOException("Gagal membuat folder Downloads")
    }
    val file = File(directory, displayName)

    return DownloadTarget(
      uri = Uri.fromFile(file),
      open = { FileOutputStream(file) },
      complete = {},
      abort = { file.delete() },
    )
  }
}

private class DownloadTarget(
  val uri: Uri,
  val open: () -> OutputStream,
  val complete: () -> Unit,
  val abort: () -> Unit,
)
