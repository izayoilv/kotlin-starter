package com.example.notesapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.model.NotesRepository
import com.example.notesapp.model.NotesResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NoteEditorViewModel
  @Inject
  constructor(
    private val repository: NotesRepository,
  ) : ViewModel() {
    private val _uiState = MutableStateFlow(NoteEditorUiState())
    val uiState: StateFlow<NoteEditorUiState> = _uiState.asStateFlow()

    private val _saved = Channel<Unit>(Channel.BUFFERED)
    val saved: Flow<Unit> = _saved.receiveAsFlow()

    fun save(title: String, body: String) {
      if (title.isBlank()) {
        _uiState.update { it.copy(titleError = true) }
        return
      }
      if (_uiState.value.isSaving) return
      viewModelScope.launch {
        _uiState.update { it.copy(isSaving = true, titleError = false, error = null) }
        when (val result = repository.add(title, body)) {
          is NotesResult.Success -> _saved.send(Unit)
          is NotesResult.Failure -> _uiState.update { it.copy(error = result.error) }
        }
        _uiState.update { it.copy(isSaving = false) }
      }
    }

    fun clearError() {
      _uiState.update { it.copy(error = null) }
    }
  }
