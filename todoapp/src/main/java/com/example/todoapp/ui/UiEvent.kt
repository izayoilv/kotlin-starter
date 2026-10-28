package com.example.todoapp.ui

import com.example.todoapp.data.Task

sealed interface UiEvent {
  data object DatabaseError : UiEvent
  data class TaskDeleted(val task: Task) : UiEvent
}
