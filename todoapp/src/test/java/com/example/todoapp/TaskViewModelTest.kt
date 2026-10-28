package com.example.todoapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.todoapp.data.Task
import com.example.todoapp.data.TaskRepository
import com.example.todoapp.ui.TaskViewModel
import com.example.todoapp.ui.UiEvent
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {
  @get:Rule
  val instantExecutorRule = InstantTaskExecutorRule()

  private val dispatcher = StandardTestDispatcher()
  private lateinit var repository: FakeTaskRepository
  private lateinit var viewModel: TaskViewModel

  @Before
  fun setUp() {
    Dispatchers.setMain(dispatcher)
    repository = FakeTaskRepository()
    viewModel = TaskViewModel(repository)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun addTaskTrimsTitleAndInserts() = runTest(dispatcher) {
    assertTrue(viewModel.addTask("  Beli susu  "))
    advanceUntilIdle()
    assertEquals(listOf("Beli susu"), repository.tasks.value.map { it.title })
  }

  @Test
  fun addTaskRejectsBlankTitle() = runTest(dispatcher) {
    assertFalse(viewModel.addTask("   "))
    advanceUntilIdle()
    assertTrue(repository.tasks.value.isEmpty())
  }

  @Test
  fun counterReflectsUnfinishedTasks() = runTest(dispatcher) {
    viewModel.addTask("A")
    viewModel.addTask("B")
    advanceUntilIdle()
    val first = repository.tasks.value.first()
    viewModel.setDone(first, true)
    advanceUntilIdle()
    assertEquals(1, viewModel.unfinishedCount.getOrAwaitValue { advanceUntilIdle() })
  }

  @Test
  fun deleteAndRestoreRoundTrip() = runTest(dispatcher) {
    viewModel.addTask("A")
    advanceUntilIdle()
    val task = repository.tasks.value.single()
    viewModel.delete(task)
    advanceUntilIdle()
    assertTrue(repository.tasks.value.isEmpty())
    viewModel.restore(task)
    advanceUntilIdle()
    assertEquals(listOf(task.id), repository.tasks.value.map { it.id })
  }

  @Test
  fun writeFailureEmitsDatabaseError() = runTest(dispatcher) {
    repository.failWrites = true
    val event = async { viewModel.events.first() }
    viewModel.addTask("Beli susu")
    advanceUntilIdle()
    assertEquals(UiEvent.DatabaseError, event.await())
  }

  @Test
  fun deleteEmitsTaskDeleted() = runTest(dispatcher) {
    viewModel.addTask("A")
    advanceUntilIdle()
    val task = repository.tasks.value.single()
    val event = async { viewModel.events.first() }
    viewModel.delete(task)
    advanceUntilIdle()
    assertEquals(UiEvent.TaskDeleted(task), event.await())
  }

  @Test
  fun readFailureEmitsDatabaseErrorAndEmptyList() = runTest(dispatcher) {
    repository.failReads = true
    val event = async { viewModel.events.first() }
    assertTrue(viewModel.tasks.getOrAwaitValue { advanceUntilIdle() }.isEmpty())
    assertEquals(UiEvent.DatabaseError, event.await())
  }
}

private class FakeTaskRepository : TaskRepository {
  val tasks = MutableStateFlow<List<Task>>(emptyList())
  var failWrites = false
  var failReads = false
  private var nextId = 1L

  override fun observeTasks(): Flow<List<Task>> = flow {
    if (failReads) error("read failed") else emitAll(tasks)
  }

  override fun observeUnfinishedCount(): Flow<Int> =
    tasks.map { list -> list.count { !it.isDone } }

  override suspend fun add(title: String) {
    failIfRequested()
    tasks.value = tasks.value + Task(id = nextId++, title = title, createdAt = nextId)
  }

  override suspend fun setDone(task: Task, done: Boolean) {
    failIfRequested()
    tasks.value = tasks.value.map { if (it.id == task.id) it.copy(isDone = done) else it }
  }

  override suspend fun delete(task: Task) {
    failIfRequested()
    tasks.value = tasks.value.filterNot { it.id == task.id }
  }

  override suspend fun restore(task: Task) {
    failIfRequested()
    tasks.value = tasks.value + task
  }

  private fun failIfRequested() {
    if (failWrites) error("write failed")
  }
}

@Suppress("UNCHECKED_CAST")
private fun <T> LiveData<T>.getOrAwaitValue(afterObserve: () -> Unit = {}): T {
  var result: T? = null
  val latch = CountDownLatch(1)
  val observer = object : Observer<T> {
    override fun onChanged(value: T) {
      result = value
      latch.countDown()
      this@getOrAwaitValue.removeObserver(this)
    }
  }
  observeForever(observer)
  afterObserve()
  check(latch.await(2, TimeUnit.SECONDS)) { "LiveData value was never set." }
  return result as T
}
