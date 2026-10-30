package com.example.postsapp.data

import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class LoadErrorTest {
  @Test
  fun ioExceptionMapsToNetwork() {
    assertEquals(LoadError.Network, IOException().toLoadError())
  }

  @Test
  fun httpExceptionMapsToHttpCode() {
    val exception = HttpException(
      Response.error<Any>(404, "".toResponseBody("text/plain".toMediaType())),
    )
    assertEquals(LoadError.Http(404), exception.toLoadError())
  }

  @Test
  fun otherExceptionMapsToUnknown() {
    assertEquals(LoadError.Unknown, IllegalStateException().toLoadError())
  }
}
