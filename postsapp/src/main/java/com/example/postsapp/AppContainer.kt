package com.example.postsapp

import com.example.postsapp.data.NetworkPostRepository
import com.example.postsapp.data.PostRepository
import com.example.postsapp.data.RetrofitClient

class AppContainer {
  val postRepository: PostRepository by lazy {
    NetworkPostRepository(RetrofitClient.api)
  }
}
