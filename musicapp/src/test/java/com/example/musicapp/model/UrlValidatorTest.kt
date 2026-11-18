package com.example.musicapp.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UrlValidatorTest {
  @Test
  fun `accepts http and https`() {
    assertTrue(UrlValidator.isValid("https://download.samplelib.com/mp3/sample-15s.mp3"))
    assertTrue(UrlValidator.isValid("http://example.com/song.mp3"))
  }

  @Test
  fun `rejects blank and non-http schemes`() {
    assertFalse(UrlValidator.isValid(""))
    assertFalse(UrlValidator.isValid("ftp://example.com/song.mp3"))
    assertFalse(UrlValidator.isValid("not a url"))
  }
}
