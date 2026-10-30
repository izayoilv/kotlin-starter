package com.example.postsapp.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.postsapp.data.PostRepository

class PostDetailViewModelFactory(
  private val repository: PostRepository,
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(PostDetailViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return PostDetailViewModel(repository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
  }
}
