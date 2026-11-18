package com.example.musicapp.model

object RetryPolicy {
  fun shouldRetry(runAttemptCount: Int): Boolean =
    runAttemptCount < DownloadContract.MAX_ATTEMPTS - 1
}
