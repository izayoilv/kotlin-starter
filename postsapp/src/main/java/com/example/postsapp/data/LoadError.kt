package com.example.postsapp.data

import retrofit2.HttpException
import java.io.IOException

sealed interface LoadError {
  data object Network : LoadError

  data class Http(
    val code: Int,
  ) : LoadError

  data object Unknown : LoadError
}

fun Throwable.toLoadError(): LoadError =
  when (this) {
    is HttpException -> LoadError.Http(code())
    is IOException -> LoadError.Network
    else -> LoadError.Unknown
  }
