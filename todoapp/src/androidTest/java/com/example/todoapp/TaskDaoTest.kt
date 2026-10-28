package com.example.todoapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todoapp.data.Task
import com.example.todoapp.data.TaskDao
import com.example.todoapp.data.TodoDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskDaoTest {
  private lateinit var db: TodoDatabase
  private lateinit var dao: TaskDao

  @Before
  fun createDb() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, TodoDatabase::class.java).build()
    dao = db.taskDao()
  }

  @After
  fun closeDb() {
    db.close()
  }

  @Test
  fun insertAndObserveAll() = runTest {
    dao.insert(Task(title = "Beli susu", createdAt = 1L))
    assertEquals(listOf("Beli susu"), dao.observeAll().first().map { it.title })
  }

  @Test
  fun unfinishedCountIgnoresDoneTasks() = runTest {
    dao.insert(Task(title = "A", createdAt = 1L))
    dao.insert(Task(title = "B", isDone = true, createdAt = 2L))
    assertEquals(1, dao.observeUnfinishedCount().first())
  }

  @Test
  fun orderingPutsUnfinishedFirstThenNewest() = runTest {
    dao.insert(Task(title = "done-new", isDone = true, createdAt = 30L))
    dao.insert(Task(title = "open-old", createdAt = 10L))
    dao.insert(Task(title = "open-new", createdAt = 20L))
    assertEquals(
      listOf("open-new", "open-old", "done-new"),
      dao.observeAll().first().map { it.title },
    )
  }

  @Test
  fun updateAndDelete() = runTest {
    dao.insert(Task(title = "A", createdAt = 1L))
    val task = dao.observeAll().first().single()
    dao.update(task.copy(isDone = true))
    assertEquals(0, dao.observeUnfinishedCount().first())
    dao.delete(task.copy(isDone = true))
    assertTrue(dao.observeAll().first().isEmpty())
  }
}
