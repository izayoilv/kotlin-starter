package com.example.todoapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.Task
import com.example.todoapp.data.TaskRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
  private val _events = Channel<UiEvent>(Channel.BUFFERED)
  val events: Flow<UiEvent> = _events.receiveAsFlow()

  val tasks: LiveData<List<Task>> = repository.observeTasks()
    .catch {
      emit(emptyList())
      _events.send(UiEvent.DatabaseError)
    }
    .asLiveData()

  val unfinishedCount: LiveData<Int> = repository.observeUnfinishedCount()
    .catch {
      emit(0)
      _events.send(UiEvent.DatabaseError)
    }
    .asLiveData()

  fun addTask(rawTitle: String): Boolean {
    val title = rawTitle.trim()
    if (title.isEmpty()) return false
    write { repository.add(title) }
    return true
  }

  fun setDone(task: Task, done: Boolean) {
    write { repository.setDone(task, done) }
  }

  fun delete(task: Task) {
    write { repository.delete(task) }
  }

  fun restore(task: Task) {
    write { repository.restore(task) }
  }

  private fun write(block: suspend () -> Unit) {
    viewModelScope.launch {
      try {
        block()
      } catch (e: CancellationException) {
        throw e
      } catch (_: Exception) {
        _events.send(UiEvent.DatabaseError)
      }
    }
  }
}
