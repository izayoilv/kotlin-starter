package com.example.todoapp

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.data.RoomTaskRepository
import com.example.todoapp.data.Task
import com.example.todoapp.data.TodoDatabase
import com.example.todoapp.databinding.ActivityMainBinding
import com.example.todoapp.ui.AddTaskDialogFragment
import com.example.todoapp.ui.TaskAdapter
import com.example.todoapp.ui.TaskViewModel
import com.example.todoapp.ui.TaskViewModelFactory
import com.example.todoapp.ui.UiEvent
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding
  private lateinit var adapter: TaskAdapter

  private val viewModel: TaskViewModel by viewModels {
    TaskViewModelFactory(
      RoomTaskRepository(TodoDatabase.getInstance(applicationContext).taskDao()),
    )
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    setSupportActionBar(binding.toolbar)
    applyFabBottomInset()

    adapter = TaskAdapter(
      onDoneChanged = viewModel::setDone,
      onDelete = ::deleteWithUndo,
    )
    binding.rvTasks.layoutManager = LinearLayoutManager(this)
    binding.rvTasks.adapter = adapter
    attachSwipeToDelete()

    binding.fabAdd.setOnClickListener {
      AddTaskDialogFragment().show(supportFragmentManager, AddTaskDialogFragment.TAG)
    }
    supportFragmentManager.setFragmentResultListener(
      AddTaskDialogFragment.REQUEST_KEY,
      this,
    ) { _, result ->
      result.getString(AddTaskDialogFragment.KEY_TITLE)?.let(viewModel::addTask)
    }

    viewModel.tasks.observe(this) { tasks ->
      adapter.submitList(tasks)
      binding.rvTasks.isVisible = tasks.isNotEmpty()
      binding.emptyState.isVisible = tasks.isEmpty()
      binding.tvUnfinishedCount.isVisible = tasks.isNotEmpty()
    }
    viewModel.unfinishedCount.observe(this) { count ->
      binding.tvUnfinishedCount.text =
        resources.getQuantityString(R.plurals.unfinished_tasks, count, count)
    }
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.events.collect { event ->
          when (event) {
            UiEvent.DatabaseError -> showDatabaseError()
          }
        }
      }
    }
  }

  private fun applyFabBottomInset() {
    val baseMargin = (binding.fabAdd.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
    ViewCompat.setOnApplyWindowInsetsListener(binding.fabAdd) { view, insets ->
      val navBarBottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
      val params = view.layoutParams as ViewGroup.MarginLayoutParams
      val target = baseMargin + navBarBottom
      if (params.bottomMargin != target) {
        params.bottomMargin = target
        view.layoutParams = params
      }
      insets
    }
  }

  private fun attachSwipeToDelete() {
    ItemTouchHelper(
      object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START) {
        override fun onMove(
          recyclerView: RecyclerView,
          viewHolder: RecyclerView.ViewHolder,
          target: RecyclerView.ViewHolder,
        ): Boolean = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
          val position = viewHolder.bindingAdapterPosition
          if (position == RecyclerView.NO_POSITION) return
          deleteWithUndo(adapter.currentList[position])
        }
      },
    ).attachToRecyclerView(binding.rvTasks)
  }

  private fun deleteWithUndo(task: Task) {
    viewModel.delete(task)
    Snackbar.make(binding.root, R.string.task_deleted, Snackbar.LENGTH_LONG)
      .setAction(R.string.undo) { viewModel.restore(task) }
      .show()
  }

  private fun showDatabaseError() {
    Snackbar.make(binding.root, R.string.error_database, Snackbar.LENGTH_LONG).show()
  }
}
