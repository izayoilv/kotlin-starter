package com.example.postsapp

import android.app.Application

class PostsApplication : Application() {
  val container: AppContainer by lazy { AppContainer() }
}
