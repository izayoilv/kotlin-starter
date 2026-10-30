package com.example.postsapp.ui.list

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

class PostListViewModel(private val repository: PostRepository) : ViewModel() {
  private val _state = MutableStateFlow<UiState<List<Post>>>(UiState.Loading)
  val state: StateFlow<UiState<List<Post>>> = _state.asStateFlow()

  init {
    load()
  }

  fun load() {
    viewModelScope.launch {
      _state.value = UiState.Loading
      repository.getPosts()
        .onSuccess { _state.value = UiState.Success(it) }
        .onFailure { _state.value = UiState.Error(it.toLoadError()) }
    }
  }
}
