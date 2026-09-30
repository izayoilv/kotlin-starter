package com.example.newsreader

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ArticleDataTest {
  @Test
  fun `articles have unique ids`() {
    val ids = ArticleData.articles.map { it.id }
    assertEquals(ids.size, ids.toSet().size)
  }

  @Test
  fun `articles have non-blank fields`() {
    ArticleData.articles.forEach { article ->
      assertTrue(article.title.isNotBlank())
      assertTrue(article.snippet.isNotBlank())
      assertTrue(article.body.isNotBlank())
    }
  }

  @Test
  fun `byId finds existing and rejects unknown`() {
    assertNotNull(ArticleData.byId(ArticleData.articles.first().id))
    assertNull(ArticleData.byId(-999))
  }
}
