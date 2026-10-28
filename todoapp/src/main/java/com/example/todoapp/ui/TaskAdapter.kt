package com.example.todoapp.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.data.Task
import com.example.todoapp.databinding.ItemTaskBinding

class TaskAdapter(
  private val onDoneChanged: (Task, Boolean) -> Unit,
  private val onDelete: (Task) -> Unit,
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(DiffCallback) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
    val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return TaskViewHolder(binding)
  }

  override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  inner class TaskViewHolder(
    private val binding: ItemTaskBinding,
  ) : RecyclerView.ViewHolder(binding.root) {
    fun bind(task: Task) {
      binding.cbDone.setOnCheckedChangeListener(null)
      binding.cbDone.isChecked = task.isDone
      binding.tvTitle.text = task.title
      binding.tvTitle.paintFlags = if (task.isDone) {
        binding.tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
      } else {
        binding.tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
      }
      binding.tvTitle.alpha = if (task.isDone) 0.6f else 1f
      binding.cbDone.setOnCheckedChangeListener { _, isChecked ->
        onDoneChanged(task, isChecked)
      }
      binding.btnDelete.setOnClickListener { onDelete(task) }
    }
  }

  private object DiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
      oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean =
      oldItem == newItem
  }
}
