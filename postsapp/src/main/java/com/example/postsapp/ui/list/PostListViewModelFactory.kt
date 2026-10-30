package com.example.postsapp.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.postsapp.data.PostRepository

class PostListViewModelFactory(
  private val repository: PostRepository,
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(PostListViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return PostListViewModel(repository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
  }
}
