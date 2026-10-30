package com.example.postsapp.data

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PostRepositoryTest {
  private lateinit var server: MockWebServer
  private lateinit var repository: PostRepository

  @Before
  fun setUp() {
    server = MockWebServer()
    server.start()
    val api = Retrofit.Builder()
      .baseUrl(server.url("/"))
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(PostApi::class.java)
    repository = NetworkPostRepository(api)
  }

  @After
  fun tearDown() {
    server.shutdown()
  }

  @Test
  fun getPostsParsesList() = runTest {
    server.enqueue(
      MockResponse().setResponseCode(200).setBody(
        """[{"userId":1,"id":1,"title":"Judul","body":"Isi"}]""",
      ),
    )
    val posts = repository.getPosts().getOrThrow()
    assertEquals(1, posts.size)
    assertEquals("Judul", posts.single().title)
    assertEquals("/posts", server.takeRequest().path)
  }

  @Test
  fun getPostParsesSinglePost() = runTest {
    server.enqueue(
      MockResponse().setResponseCode(200).setBody(
        """{"userId":1,"id":7,"title":"Detail","body":"Isi lengkap"}""",
      ),
    )
    val post = repository.getPost(7).getOrThrow()
    assertEquals(7, post.id)
    assertEquals("Isi lengkap", post.body)
    assertEquals("/posts/7", server.takeRequest().path)
  }

  @Test
  fun httpErrorBecomesFailure() = runTest {
    server.enqueue(MockResponse().setResponseCode(500))
    val result = repository.getPosts()
    assertTrue(result.isFailure)
    assertTrue(result.exceptionOrNull() is HttpException)
  }

  @Test
  fun malformedJsonBecomesFailure() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody("not json"))
    assertTrue(repository.getPosts().isFailure)
  }
}
