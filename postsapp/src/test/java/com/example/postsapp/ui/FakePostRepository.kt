package com.example.postsapp.ui

import com.example.postsapp.data.Post
import com.example.postsapp.data.PostRepository

class FakePostRepository : PostRepository {
  var postsResult: Result<List<Post>> = Result.success(emptyList())
  var postResult: Result<Post> = Result.success(Post(1, 1, "Judul", "Isi"))
  var getPostsCalls = 0
  var getPostCalls = 0

  override suspend fun getPosts(): Result<List<Post>> {
    getPostsCalls++
    return postsResult
  }

  override suspend fun getPost(id: Int): Result<Post> {
    getPostCalls++
    return postResult
  }
}
