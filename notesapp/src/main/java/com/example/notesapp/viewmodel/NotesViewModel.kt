package com.example.notesapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.model.NotesError
import com.example.notesapp.model.NotesRepository
import com.example.notesapp.model.NotesResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class NotesViewModel
  @Inject
  constructor(
    private val repository: NotesRepository,
  ) : ViewModel() {
    private val refreshing = MutableStateFlow(false)
    private val error = MutableStateFlow<NotesError?>(null)

    val uiState: StateFlow<NotesUiState> =
      combine(repository.notes, refreshing, error) { notes, isRefreshing, error ->
        NotesUiState(notes, isRefreshing, error)
      }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NotesUiState())

    init {
      refresh()
    }

    fun refresh() {
      viewModelScope.launch {
        refreshing.value = true
        when (val result = repository.refresh()) {
          is NotesResult.Success -> error.value = null
          is NotesResult.Failure -> error.value = result.error
        }
        refreshing.value = false
      }
    }

    fun delete(id: Long) {
      viewModelScope.launch {
        when (val result = repository.delete(id)) {
          is NotesResult.Success -> Unit
          is NotesResult.Failure -> error.value = result.error
        }
      }
    }

    fun clearError() {
      error.value = null
    }
  }
