package com.example.musicapp.model

import java.net.URI

object UrlValidator {
  fun isValid(url: String): Boolean =
    runCatching {
      val uri = URI(url.trim())
      (uri.scheme == "http" || uri.scheme == "https") && !uri.host.isNullOrBlank()
    }.getOrDefault(false)
}
