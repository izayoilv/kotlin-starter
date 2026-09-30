package com.example.newsreader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsreader.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
  private var _binding: FragmentHomeBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    _binding = FragmentHomeBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val adapter = ArticleAdapter { article ->
      findNavController().navigate(
        R.id.action_home_to_detail,
        bundleOf("articleId" to article.id),
      )
    }
    binding.articleList.layoutManager = LinearLayoutManager(requireContext())
    binding.articleList.adapter = adapter
    adapter.submitList(ArticleData.articles)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}
