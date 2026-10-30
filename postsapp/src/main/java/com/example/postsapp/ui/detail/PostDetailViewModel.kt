package com.example.postsapp.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.postsapp.data.Post
import com.example.postsapp.data.PostRepository
import com.example.postsapp.data.toLoadError
import com.example.postsapp.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostDetailViewModel(private val repository: PostRepository) : ViewModel() {
  private val _state = MutableStateFlow<UiState<Post>>(UiState.Loading)
  val state: StateFlow<UiState<Post>> = _state.asStateFlow()
  private var currentId: Int? = null

  fun load(postId: Int) {
    val current = _state.value
    if (currentId == postId && (current is UiState.Loading || current is UiState.Success)) {
      return
    }
    currentId = postId
    viewModelScope.launch {
      _state.value = UiState.Loading
      repository.getPost(postId)
        .onSuccess { _state.value = UiState.Success(it) }
        .onFailure { _state.value = UiState.Error(it.toLoadError()) }
    }
  }
}
