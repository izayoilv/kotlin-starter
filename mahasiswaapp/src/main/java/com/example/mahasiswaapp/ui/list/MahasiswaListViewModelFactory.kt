package com.example.mahasiswaapp.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mahasiswaapp.data.MahasiswaRepository

class MahasiswaListViewModelFactory(
  private val repository: MahasiswaRepository,
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(MahasiswaListViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return MahasiswaListViewModel(repository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
  }
}
