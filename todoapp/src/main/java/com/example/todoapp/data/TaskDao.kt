package com.example.todoapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
  @Query("SELECT * FROM tasks ORDER BY isDone ASC, createdAt DESC")
  fun observeAll(): Flow<List<Task>>

  @Query("SELECT COUNT(*) FROM tasks WHERE isDone = 0")
  fun observeUnfinishedCount(): Flow<Int>

  @Insert
  suspend fun insert(task: Task): Long

  @Update
  suspend fun update(task: Task)

  @Delete
  suspend fun delete(task: Task)
}
