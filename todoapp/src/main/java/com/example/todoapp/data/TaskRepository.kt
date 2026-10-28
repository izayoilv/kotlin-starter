package com.example.todoapp.data

import kotlinx.coroutines.flow.Flow

interface TaskRepository {
  fun observeTasks(): Flow<List<Task>>
  fun observeUnfinishedCount(): Flow<Int>
  suspend fun add(title: String)
  suspend fun setDone(task: Task, done: Boolean)
  suspend fun delete(task: Task)
  suspend fun restore(task: Task)
}

class RoomTaskRepository(private val dao: TaskDao) : TaskRepository {
  override fun observeTasks(): Flow<List<Task>> = dao.observeAll()

  override fun observeUnfinishedCount(): Flow<Int> = dao.observeUnfinishedCount()

  override suspend fun add(title: String) {
    dao.insert(Task(title = title))
  }

  override suspend fun setDone(task: Task, done: Boolean) {
    dao.update(task.copy(isDone = done))
  }

  override suspend fun delete(task: Task) {
    dao.delete(task)
  }

  override suspend fun restore(task: Task) {
    dao.insert(task)
  }
}
