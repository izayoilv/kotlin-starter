package com.example.postsapp.ui.list

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.postsapp.PostsApplication
import com.example.postsapp.R
import com.example.postsapp.data.LoadError
import com.example.postsapp.data.Post
import com.example.postsapp.databinding.FragmentPostListBinding
import com.example.postsapp.ui.NavArgs
import com.example.postsapp.ui.UiState
import kotlinx.coroutines.launch

class PostListFragment : Fragment(R.layout.fragment_post_list) {
  private var _binding: FragmentPostListBinding? = null
  private val binding get() = _binding!!

  private val viewModel: PostListViewModel by viewModels {
    PostListViewModelFactory(
      (requireActivity().application as PostsApplication).container.postRepository,
    )
  }

  private lateinit var adapter: PostAdapter

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    _binding = FragmentPostListBinding.bind(view)

    adapter = PostAdapter { postId ->
      findNavController().navigate(
        R.id.action_list_to_detail,
        Bundle().apply { putInt(NavArgs.POST_ID, postId) },
      )
    }
    binding.rvPosts.layoutManager = LinearLayoutManager(requireContext())
    binding.rvPosts.adapter = adapter
    binding.btnRetry.setOnClickListener { viewModel.load() }

    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.state.collect { state -> render(state) }
      }
    }
  }

  private fun render(state: UiState<List<Post>>) {
    binding.progressBar.isVisible = state is UiState.Loading
    binding.errorGroup.isVisible = state is UiState.Error
    binding.emptyGroup.isVisible = state is UiState.Success && state.data.isEmpty()
    binding.rvPosts.isVisible = state is UiState.Success && state.data.isNotEmpty()

    when (state) {
      UiState.Loading -> Unit
      is UiState.Success -> adapter.submitList(state.data)
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
