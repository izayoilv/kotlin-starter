package com.example.postsapp.ui.detail

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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PostDetailViewModelTest {
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
  fun loadSuccessExposesPost() = runTest(dispatcher) {
    val post = Post(1, 7, "Detail", "Isi lengkap")
    repository.postResult = Result.success(post)

    val viewModel = PostDetailViewModel(repository)
    viewModel.load(7)
    advanceUntilIdle()

    assertEquals(UiState.Success(post), viewModel.state.value)
    assertEquals(1, repository.getPostCalls)
  }

  @Test
  fun httpFailureExposesCode() = runTest(dispatcher) {
    repository.postResult = Result.failure(httpException(404))

    val viewModel = PostDetailViewModel(repository)
    viewModel.load(7)
    advanceUntilIdle()

    assertEquals(UiState.Error(LoadError.Http(404)), viewModel.state.value)
  }

  @Test
  fun sameIdSuccessDoesNotRefetch() = runTest(dispatcher) {
    repository.postResult = Result.success(Post(1, 7, "Detail", "Isi"))
    val viewModel = PostDetailViewModel(repository)
    viewModel.load(7)
    advanceUntilIdle()

    viewModel.load(7)
    advanceUntilIdle()

    assertEquals(1, repository.getPostCalls)
  }

  @Test
  fun errorAllowsSameIdRetry() = runTest(dispatcher) {
    repository.postResult = Result.failure(IOException("offline"))
    val viewModel = PostDetailViewModel(repository)
    viewModel.load(7)
    advanceUntilIdle()
    assertTrue(viewModel.state.value is UiState.Error)

    repository.postResult = Result.success(Post(1, 7, "Detail", "Isi"))
    viewModel.load(7)
    advanceUntilIdle()

    assertTrue(viewModel.state.value is UiState.Success)
    assertEquals(2, repository.getPostCalls)
  }

  private fun httpException(code: Int): retrofit2.HttpException =
    retrofit2.HttpException(
      retrofit2.Response.error<Any>(
        code,
        "".toResponseBody("text/plain".toMediaType()),
      ),
    )
}
