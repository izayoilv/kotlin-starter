package com.example.mahasiswaapp.ui

import android.content.Context
import com.example.mahasiswaapp.R
import com.example.mahasiswaapp.data.ApiException
import retrofit2.HttpException
import java.io.IOException

fun Throwable.toUserMessage(context: Context): String =
  when (this) {
    is ApiException -> message?.takeIf { it.isNotBlank() } ?: context.getString(R.string.error_generic)
    is IOException -> context.getString(R.string.error_network)
    is HttpException -> context.getString(R.string.error_http, code())
    else -> context.getString(R.string.error_generic)
  }
