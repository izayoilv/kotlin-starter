package com.example.mahasiswaapp.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mahasiswaapp.data.Mahasiswa
import com.example.mahasiswaapp.data.MahasiswaRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class ListUiState(
  val isLoading: Boolean = false,
  val items: List<Mahasiswa> = emptyList(),
  val error: Throwable? = null,
)

sealed interface ListEvent {
  data class Message(val text: String) : ListEvent
  data class Error(val throwable: Throwable) : ListEvent
}

class MahasiswaListViewModel(
  private val repository: MahasiswaRepository,
) : ViewModel() {
  private val _state = MutableStateFlow(ListUiState(isLoading = true))
  val state: StateFlow<ListUiState> = _state.asStateFlow()

  private val _events = Channel<ListEvent>(Channel.BUFFERED)
  val events: Flow<ListEvent> = _events.receiveAsFlow()

  init {
    load()
  }

  fun load() {
    viewModelScope.launch {
      _state.value = _state.value.copy(isLoading = true, error = null)
      repository.getAll()
        .onSuccess { _state.value = ListUiState(items = it) }
        .onFailure {
          _state.value = _state.value.copy(isLoading = false, error = it)
          if (_state.value.items.isNotEmpty()) {
            _events.send(ListEvent.Error(it))
          }
        }
    }
  }

  fun delete(mahasiswa: Mahasiswa) {
    viewModelScope.launch {
      _state.value = _state.value.copy(isLoading = true)
      repository.delete(mahasiswa)
        .onSuccess { message ->
          _state.value = _state.value.copy(isLoading = false)
          _events.send(ListEvent.Message(message))
          load()
        }
        .onFailure {
          _state.value = _state.value.copy(isLoading = false)
          _events.send(ListEvent.Error(it))
        }
    }
  }
}
