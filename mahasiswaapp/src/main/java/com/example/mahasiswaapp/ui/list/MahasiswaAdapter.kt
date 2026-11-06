package com.example.mahasiswaapp.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mahasiswaapp.R
import com.example.mahasiswaapp.data.Mahasiswa
import com.example.mahasiswaapp.databinding.ItemMahasiswaBinding

class MahasiswaAdapter(
  private val onClick: (Mahasiswa) -> Unit,
  private val onEdit: (Mahasiswa) -> Unit,
  private val onDelete: (Mahasiswa) -> Unit,
) : ListAdapter<Mahasiswa, MahasiswaAdapter.ViewHolder>(DiffCallback) {
  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int,
  ): ViewHolder {
    val binding = ItemMahasiswaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return ViewHolder(binding)
  }

  override fun onBindViewHolder(
    holder: ViewHolder,
    position: Int,
  ) {
    holder.bind(getItem(position))
  }

  inner class ViewHolder(
    private val binding: ItemMahasiswaBinding,
  ) : RecyclerView.ViewHolder(binding.root) {
    fun bind(mahasiswa: Mahasiswa) {
      binding.tvNim.text = binding.root.context.getString(R.string.nim_label, mahasiswa.nim)
      binding.tvNama.text = mahasiswa.nama
      binding.tvJurusan.text = binding.root.context.getString(R.string.jurusan_label, mahasiswa.jurusan)
      binding.tvEmail.text = binding.root.context.getString(R.string.email_label, mahasiswa.email)
      binding.root.setOnClickListener { onClick(mahasiswa) }
      binding.btnEdit.setOnClickListener { onEdit(mahasiswa) }
      binding.btnDelete.setOnClickListener { onDelete(mahasiswa) }
    }
  }

  private object DiffCallback : DiffUtil.ItemCallback<Mahasiswa>() {
    override fun areItemsTheSame(
      oldItem: Mahasiswa,
      newItem: Mahasiswa,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
      oldItem: Mahasiswa,
      newItem: Mahasiswa,
    ): Boolean = oldItem == newItem
  }
}
