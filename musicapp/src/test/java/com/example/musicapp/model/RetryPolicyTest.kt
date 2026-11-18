package com.example.musicapp.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RetryPolicyTest {
  @Test
  fun `three attempts means the initial run plus two retries`() {
    assertTrue(RetryPolicy.shouldRetry(0))
    assertTrue(RetryPolicy.shouldRetry(1))
    assertFalse(RetryPolicy.shouldRetry(2))
    assertFalse(RetryPolicy.shouldRetry(3))
  }
}
