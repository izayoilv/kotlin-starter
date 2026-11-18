package com.example.musicapp.model

import java.net.URI

object FileName {
  private val audioExtensions = setOf("mp3", "m4a", "ogg", "wav", "aac", "flac")

  fun sanitize(name: String): String =
    name
      .substringAfterLast('/')
      .replace(Regex("[^A-Za-z0-9._-]"), "_")
      .trim('_', '.')
      .ifBlank { "music_${System.currentTimeMillis()}" }

  fun extensionFromUrl(url: String): String =
    runCatching {
      URI(url).path
        .substringAfterLast('.', "")
        .lowercase()
        .takeIf { it in audioExtensions }
    }.getOrNull() ?: "mp3"

  fun withExtension(
    name: String,
    url: String,
  ): String {
    val sanitized = sanitize(name)
    val extension = sanitized.substringAfterLast('.', "").lowercase()
    return if (extension in audioExtensions) {
      sanitized
    } else {
      "$sanitized.${extensionFromUrl(url)}"
    }
  }

  fun mimeType(extension: String): String =
    when (extension.lowercase()) {
      "mp3" -> "audio/mpeg"
      "m4a" -> "audio/mp4"
      "ogg" -> "audio/ogg"
      "wav" -> "audio/wav"
      "aac" -> "audio/aac"
      "flac" -> "audio/flac"
      else -> "audio/mpeg"
    }
}
