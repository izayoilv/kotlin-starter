package com.example.postsapp.ui.detail

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.postsapp.PostsApplication
import com.example.postsapp.R
import com.example.postsapp.data.LoadError
import com.example.postsapp.data.Post
import com.example.postsapp.databinding.FragmentPostDetailBinding
import com.example.postsapp.ui.NavArgs
import com.example.postsapp.ui.UiState
import kotlinx.coroutines.launch

class PostDetailFragment : Fragment(R.layout.fragment_post_detail) {
  private var _binding: FragmentPostDetailBinding? = null
  private val binding get() = _binding!!

  private val viewModel: PostDetailViewModel by viewModels {
    PostDetailViewModelFactory(
      (requireActivity().application as PostsApplication).container.postRepository,
    )
  }

  private val postId: Int by lazy { requireArguments().getInt(NavArgs.POST_ID) }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    _binding = FragmentPostDetailBinding.bind(view)

    binding.btnRetry.setOnClickListener { viewModel.load(postId) }
    viewModel.load(postId)

    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.state.collect { state -> render(state) }
      }
    }
  }

  private fun render(state: UiState<Post>) {
    binding.progressBar.isVisible = state is UiState.Loading
    binding.detailContent.isVisible = state is UiState.Success
    binding.errorGroup.isVisible = state is UiState.Error

    when (state) {
      UiState.Loading -> Unit
      is UiState.Success -> {
        binding.tvPostId.text = getString(R.string.post_number, state.data.id)
        binding.tvTitle.text = state.data.title
        binding.tvBody.text = state.data.body
      }
      is UiState.Error -> binding.tvError.text = state.error.asMessage()
    }
  }

  private fun LoadError.asMessage(): String = when (this) {
    LoadError.Network -> getString(R.string.error_network)
    is LoadError.Http -> getString(R.string.error_http, code)
    LoadError.Unknown -> getString(R.string.error_unknown)
  }

  override fun onDestroyView() {
    _binding = null
    super.onDestroyView()
  }
}
