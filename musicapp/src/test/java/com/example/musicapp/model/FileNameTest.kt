package com.example.musicapp.model

import org.junit.Assert.assertEquals
import org.junit.Test

class FileNameTest {
  private val url = "https://download.samplelib.com/mp3/sample-15s.mp3"

  @Test
  fun `sanitize strips path and unsafe characters`() {
    assertEquals("song_file.mp3", FileName.sanitize("a/song file.mp3"))
  }

  @Test
  fun `withExtension keeps a known audio extension`() {
    assertEquals("song.m4a", FileName.withExtension("song.m4a", url))
  }

  @Test
  fun `withExtension takes the URL extension`() {
    assertEquals("song.m4a", FileName.withExtension("song", "https://x/y/song.m4a?x=1"))
  }

  @Test
  fun `withExtension falls back to mp3`() {
    assertEquals("song.mp3", FileName.withExtension("song", "https://x/y/file.bin"))
  }

  @Test
  fun `mimeType maps audio extensions`() {
    assertEquals("audio/mpeg", FileName.mimeType("mp3"))
    assertEquals("audio/mp4", FileName.mimeType("m4a"))
    assertEquals("audio/ogg", FileName.mimeType("ogg"))
  }
}
