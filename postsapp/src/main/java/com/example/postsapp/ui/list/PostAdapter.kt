package com.example.postsapp.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.postsapp.R
import com.example.postsapp.data.Post
import com.example.postsapp.databinding.ItemPostBinding

class PostAdapter(
  private val onClick: (Int) -> Unit,
) : ListAdapter<Post, PostAdapter.PostViewHolder>(DiffCallback) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
    val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return PostViewHolder(binding)
  }

  override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  inner class PostViewHolder(
    private val binding: ItemPostBinding,
  ) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
      binding.tvPostId.text = binding.root.context.getString(R.string.post_number, post.id)
      binding.tvTitle.text = post.title
      binding.tvBodyPreview.text = post.body
      binding.root.setOnClickListener { onClick(post.id) }
    }
  }

  private object DiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem == newItem
  }
}
