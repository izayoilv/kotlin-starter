package com.example.mahasiswaapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mahasiswaapp.data.Mahasiswa
import com.example.mahasiswaapp.databinding.ActivityMainBinding
import com.example.mahasiswaapp.ui.list.ListEvent
import com.example.mahasiswaapp.ui.list.ListUiState
import com.example.mahasiswaapp.ui.list.MahasiswaAdapter
import com.example.mahasiswaapp.ui.list.MahasiswaListViewModel
import com.example.mahasiswaapp.ui.list.MahasiswaListViewModelFactory
import com.example.mahasiswaapp.ui.toUserMessage
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding
  private lateinit var adapter: MahasiswaAdapter

  private val viewModel: MahasiswaListViewModel by viewModels {
    MahasiswaListViewModelFactory(
      (application as MahasiswaApplication).container.repository,
    )
  }

  private val formLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult(),
  ) { result ->
    if (result.resultCode == RESULT_OK) viewModel.load()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    setSupportActionBar(binding.toolbar)

    adapter = MahasiswaAdapter(
      onClick = { openDetail(it) },
      onEdit = { openForm(it) },
      onDelete = { confirmDelete(it) },
    )
    binding.rvMahasiswa.layoutManager = LinearLayoutManager(this)
    binding.rvMahasiswa.adapter = adapter
    binding.fabAdd.setOnClickListener { openForm(null) }
    binding.btnRetry.setOnClickListener { viewModel.load() }
    binding.swipeRefresh.setOnRefreshListener { viewModel.load() }

    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        launch {
          viewModel.state.collect { state -> render(state) }
        }
        launch {
          viewModel.events.collect { event ->
            when (event) {
              is ListEvent.Message ->
                Snackbar.make(
                  binding.root,
                  event.text.ifBlank { getString(R.string.success_generic) },
                  Snackbar.LENGTH_SHORT,
                ).show()
              is ListEvent.Error ->
                Snackbar.make(
                  binding.root,
                  event.throwable.toUserMessage(this@MainActivity),
                  Snackbar.LENGTH_LONG,
                ).show()
            }
          }
        }
      }
    }
  }

  private fun render(state: ListUiState) {
    adapter.submitList(state.items)
    binding.progressBar.isVisible = state.isLoading && state.items.isEmpty()
    binding.swipeRefresh.isRefreshing = state.isLoading && state.items.isNotEmpty()
    binding.rvMahasiswa.isVisible = state.items.isNotEmpty()
    binding.emptyView.isVisible = !state.isLoading && state.items.isEmpty() && state.error == null
    binding.errorGroup.isVisible = !state.isLoading && state.items.isEmpty() && state.error != null
    state.error?.let { binding.tvError.text = it.toUserMessage(this) }
  }

  private fun openForm(mahasiswa: Mahasiswa?) {
    val intent = Intent(this, FormActivity::class.java)
    mahasiswa?.let { intent.putExtra(FormActivity.EXTRA_MAHASISWA, it) }
    formLauncher.launch(intent)
  }

  private fun openDetail(mahasiswa: Mahasiswa) {
    startActivity(
      Intent(this, DetailActivity::class.java)
        .putExtra(DetailActivity.EXTRA_MAHASISWA, mahasiswa),
    )
  }

  private fun confirmDelete(mahasiswa: Mahasiswa) {
    MaterialAlertDialogBuilder(this)
      .setTitle(R.string.delete_title)
      .setMessage(getString(R.string.delete_message, mahasiswa.nama))
      .setPositiveButton(R.string.delete) { _, _ -> viewModel.delete(mahasiswa) }
      .setNegativeButton(R.string.cancel, null)
      .show()
  }
}
