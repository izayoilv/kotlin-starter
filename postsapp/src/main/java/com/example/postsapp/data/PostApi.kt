package com.example.postsapp.data

import retrofit2.http.GET
import retrofit2.http.Path

interface PostApi {
  @GET("posts")
  suspend fun getPosts(): List<Post>

  @GET("posts/{id}")
  suspend fun getPost(@Path("id") id: Int): Post
}
