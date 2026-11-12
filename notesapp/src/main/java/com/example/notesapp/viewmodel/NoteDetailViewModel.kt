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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NoteDetailViewModel
  @Inject
  constructor(
    private val repository: NotesRepository,
  ) : ViewModel() {
    private val noteId = MutableStateFlow<Long?>(null)
    private val local = MutableStateFlow(NoteDetailUiState(isLoading = true))

    val uiState: StateFlow<NoteDetailUiState> =
      combine(
        noteId.filterNotNull().flatMapLatest(repository::observeNote),
        local,
      ) { note, state ->
        state.copy(note = note, isLoading = false)
      }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NoteDetailUiState(isLoading = true))

    private val _deleted = Channel<Unit>(Channel.BUFFERED)
    val deleted: Flow<Unit> = _deleted.receiveAsFlow()

    fun load(id: Long) {
      noteId.value = id
    }

    fun delete() {
      val id = noteId.value ?: return
      if (local.value.isDeleting) return
      viewModelScope.launch {
        local.update { it.copy(isDeleting = true, error = null) }
        when (val result = repository.delete(id)) {
          is NotesResult.Success -> _deleted.send(Unit)
          is NotesResult.Failure -> local.update { it.copy(error = result.error) }
        }
        local.update { it.copy(isDeleting = false) }
      }
    }

    fun clearError() {
      local.update { it.copy(error = null) }
    }
  }
