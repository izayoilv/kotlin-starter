package com.example.mahasiswaapp.ui.form

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

sealed interface FormEvent {
  data class Saved(val message: String) : FormEvent
  data class Error(val throwable: Throwable) : FormEvent
}

class FormViewModel(
  private val repository: MahasiswaRepository,
) : ViewModel() {
  private val _isSaving = MutableStateFlow(false)
  val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

  private val _events = Channel<FormEvent>(Channel.BUFFERED)
  val events: Flow<FormEvent> = _events.receiveAsFlow()

  fun save(mahasiswa: Mahasiswa, isEdit: Boolean) {
    if (_isSaving.value) return
    viewModelScope.launch {
      _isSaving.value = true
      val result = if (isEdit) repository.update(mahasiswa) else repository.create(mahasiswa)
      _isSaving.value = false
      result
        .onSuccess { _events.send(FormEvent.Saved(it)) }
        .onFailure { _events.send(FormEvent.Error(it)) }
    }
  }
}
