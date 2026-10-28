package com.example.todoapp.ui

sealed interface UiEvent {
  data object DatabaseError : UiEvent
}
