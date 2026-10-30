package com.example.postsapp.ui

import com.example.postsapp.data.LoadError

sealed interface UiState<out T> {
  data object Loading : UiState<Nothing>
  data class Success<T>(val data: T) : UiState<T>
  data class Error(val error: LoadError) : UiState<Nothing>
}
