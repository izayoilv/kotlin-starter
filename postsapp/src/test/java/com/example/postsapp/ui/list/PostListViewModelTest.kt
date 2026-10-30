package com.example.postsapp.ui.list

import com.example.postsapp.data.LoadError
import com.example.postsapp.data.Post
import com.example.postsapp.ui.FakePostRepository
import com.example.postsapp.ui.UiState
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PostListViewModelTest {
  private val dispatcher = StandardTestDispatcher()
  private lateinit var repository: FakePostRepository

  @Before
  fun setUp() {
    Dispatchers.setMain(dispatcher)
    repository = FakePostRepository()
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun loadSuccessExposesPosts() = runTest(dispatcher) {
    val posts = listOf(Post(1, 1, "Judul", "Isi"))
    repository.postsResult = Result.success(posts)

    val viewModel = PostListViewModel(repository)
    advanceUntilIdle()

    assertEquals(UiState.Success(posts), viewModel.state.value)
  }

  @Test
  fun emptyListIsSuccess() = runTest(dispatcher) {
    repository.postsResult = Result.success(emptyList())

    val viewModel = PostListViewModel(repository)
    advanceUntilIdle()

    assertEquals(UiState.Success(emptyList<Post>()), viewModel.state.value)
  }

  @Test
  fun networkFailureExposesNetworkError() = runTest(dispatcher) {
    repository.postsResult = Result.failure(IOException("offline"))

    val viewModel = PostListViewModel(repository)
    advanceUntilIdle()

    assertEquals(UiState.Error(LoadError.Network), viewModel.state.value)
  }

  @Test
  fun retryAfterFailureLoadsAgain() = runTest(dispatcher) {
    repository.postsResult = Result.failure(IOException("offline"))
    val viewModel = PostListViewModel(repository)
    advanceUntilIdle()
    assertTrue(viewModel.state.value is UiState.Error)

    repository.postsResult = Result.success(listOf(Post(1, 1, "Judul", "Isi")))
    viewModel.load()
    advanceUntilIdle()

    assertTrue(viewModel.state.value is UiState.Success)
    assertEquals(2, repository.getPostsCalls)
  }
}
