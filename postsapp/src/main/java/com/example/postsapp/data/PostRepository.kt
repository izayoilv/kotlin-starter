package com.example.postsapp.data

import kotlinx.coroutines.CancellationException

interface PostRepository {
  suspend fun getPosts(): Result<List<Post>>
  suspend fun getPost(id: Int): Result<Post>
}

class NetworkPostRepository(private val api: PostApi) : PostRepository {
  override suspend fun getPosts(): Result<List<Post>> = safeCall { api.getPosts() }

  override suspend fun getPost(id: Int): Result<Post> = safeCall { api.getPost(id) }

  private suspend fun <T> safeCall(block: suspend () -> T): Result<T> =
    try {
      Result.success(block())
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Result.failure(e)
    }
}
