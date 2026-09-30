package com.example.newsreader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.newsreader.databinding.FragmentArticleDetailBinding

class ArticleDetailFragment : Fragment() {
  private var _binding: FragmentArticleDetailBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    _binding = FragmentArticleDetailBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val articleId = arguments?.getInt("articleId") ?: -1
    val article = ArticleData.byId(articleId)
    if (article == null) {
      binding.detailTitle.text = getString(R.string.article_not_found)
      binding.detailBody.text = ""
    } else {
      binding.detailTitle.text = article.title
      binding.detailBody.text = article.body
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}
