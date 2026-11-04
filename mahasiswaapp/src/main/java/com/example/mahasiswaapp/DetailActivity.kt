package com.example.mahasiswaapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import com.example.mahasiswaapp.data.Mahasiswa
import com.example.mahasiswaapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val mahasiswa = IntentCompat.getSerializableExtra(
      intent,
      EXTRA_MAHASISWA,
      Mahasiswa::class.java,
    )
    if (mahasiswa == null) {
      finish()
      return
    }

    val binding = ActivityDetailBinding.inflate(layoutInflater)
    setContentView(binding.root)
    binding.toolbar.setNavigationOnClickListener { finish() }

    binding.tvNim.text = getString(R.string.nim_label, mahasiswa.nim)
    binding.tvNama.text = mahasiswa.nama
    binding.tvJurusan.text = mahasiswa.jurusan
    binding.tvEmail.text = mahasiswa.email
  }

  companion object {
    const val EXTRA_MAHASISWA = "mahasiswa"
  }
}
