package com.example.contact

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.contact.databinding.ItemContactBinding

class ContactAdapter : ListAdapter<Contact, ContactAdapter.ContactViewHolder>(DiffCallback) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
    val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return ContactViewHolder(binding)
  }

  override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  class ContactViewHolder(
    private val binding: ItemContactBinding,
  ) : RecyclerView.ViewHolder(binding.root) {
    fun bind(contact: Contact) {
      binding.avatar.text = contact.name.take(1).uppercase()
      binding.name.text = contact.name
      binding.phone.text = contact.phone
    }
  }

  private object DiffCallback : DiffUtil.ItemCallback<Contact>() {
    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean =
      oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean =
      oldItem == newItem
  }
}
