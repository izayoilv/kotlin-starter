package com.example.mahasiswaapp

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mahasiswaapp.data.Mahasiswa
import com.example.mahasiswaapp.databinding.ActivityFormBinding
import com.example.mahasiswaapp.ui.form.FormEvent
import com.example.mahasiswaapp.ui.form.FormViewModel
import com.example.mahasiswaapp.ui.form.FormViewModelFactory
import com.example.mahasiswaapp.ui.toUserMessage
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class FormActivity : AppCompatActivity() {
  private lateinit var binding: ActivityFormBinding
  private var editing: Mahasiswa? = null

  private val viewModel: FormViewModel by viewModels {
    FormViewModelFactory(
      (application as MahasiswaApplication).container.repository,
    )
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityFormBinding.inflate(layoutInflater)
    setContentView(binding.root)

    editing = IntentCompat.getSerializableExtra(intent, EXTRA_MAHASISWA, Mahasiswa::class.java)

    binding.toolbar.title =
      getString(if (editing == null) R.string.title_add else R.string.title_edit)
    binding.toolbar.setNavigationOnClickListener { finish() }

    editing?.let { mahasiswa ->
      binding.etNim.setText(mahasiswa.nim)
      binding.etNama.setText(mahasiswa.nama)
      binding.etJurusan.setText(mahasiswa.jurusan)
      binding.etEmail.setText(mahasiswa.email)
    }

    binding.btnSave.setOnClickListener { save() }

    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        launch {
          viewModel.isSaving.collect { saving ->
            binding.btnSave.isEnabled = !saving
            binding.btnSave.text = getString(if (saving) R.string.saving else R.string.save)
            binding.progressBar.isVisible = saving
          }
        }
        launch {
          viewModel.events.collect { event ->
            when (event) {
              is FormEvent.Saved -> {
                Toast.makeText(
                  this@FormActivity,
                  event.message.ifBlank { getString(R.string.success_generic) },
                  Toast.LENGTH_SHORT,
                ).show()
                setResult(RESULT_OK)
                finish()
              }
              is FormEvent.Error ->
                Snackbar.make(
                  binding.root,
                  event.throwable.toUserMessage(this@FormActivity),
                  Snackbar.LENGTH_LONG,
                ).show()
            }
          }
        }
      }
    }
  }

  private fun save() {
    val nim = binding.etNim.text?.toString()?.trim().orEmpty()
    val nama = binding.etNama.text?.toString()?.trim().orEmpty()
    val jurusan = binding.etJurusan.text?.toString()?.trim().orEmpty()
    val email = binding.etEmail.text?.toString()?.trim().orEmpty()

    binding.tilNim.error = null
    binding.tilNama.error = null
    binding.tilJurusan.error = null
    binding.tilEmail.error = null

    var valid = true
    if (nim.isEmpty()) {
      binding.tilNim.error = getString(R.string.error_nim_required)
      valid = false
    }
    if (nama.isEmpty()) {
      binding.tilNama.error = getString(R.string.error_nama_required)
      valid = false
    }
    if (jurusan.isEmpty()) {
      binding.tilJurusan.error = getString(R.string.error_jurusan_required)
      valid = false
    }
    when {
      email.isEmpty() -> {
        binding.tilEmail.error = getString(R.string.error_email_required)
        valid = false
      }
      !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
        binding.tilEmail.error = getString(R.string.error_email_invalid)
        valid = false
      }
    }
    if (!valid) return

    viewModel.save(
      Mahasiswa(
        id = editing?.id ?: 0,
        nim = nim,
        nama = nama,
        jurusan = jurusan,
        email = email,
      ),
      editing != null,
    )
  }

  companion object {
    const val EXTRA_MAHASISWA = "mahasiswa"
  }
}
