package com.example.mahasiswaapp.ui.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mahasiswaapp.data.MahasiswaRepository

class FormViewModelFactory(
  private val repository: MahasiswaRepository,
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(FormViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return FormViewModel(repository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
  }
}
