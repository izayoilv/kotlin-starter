package com.example.contact

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contact.databinding.ActivityMainBinding
import com.example.contact.databinding.DialogContactBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : ComponentActivity() {
  private lateinit var binding: ActivityMainBinding
  private val viewModel: ContactViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val adapter = ContactAdapter(
      onClick = { showContactDialog(it) },
      onLongClick = { confirmDelete(it) },
    )
    binding.contactList.layoutManager = LinearLayoutManager(this)
    binding.contactList.adapter = adapter
    binding.addButton.setOnClickListener { showContactDialog(null) }

    viewModel.contacts.observe(this) { contacts ->
      adapter.submitList(contacts)
      binding.emptyState.visibility = if (contacts.isEmpty()) View.VISIBLE else View.GONE
    }
  }

  private fun showContactDialog(existing: Contact?) {
    val dialogBinding = DialogContactBinding.inflate(layoutInflater)
    existing?.let {
      dialogBinding.nameInput.setText(it.name)
      dialogBinding.phoneInput.setText(it.phone)
    }

    val dialog = MaterialAlertDialogBuilder(this)
      .setTitle(if (existing == null) R.string.add_contact else R.string.edit_contact)
      .setView(dialogBinding.root)
      .setNegativeButton(R.string.cancel, null)
      .setPositiveButton(R.string.save, null)
      .create()

    dialog.show()
    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
      val name = dialogBinding.nameInput.text?.toString().orEmpty().trim()
      val phone = dialogBinding.phoneInput.text?.toString().orEmpty().trim()
      if (validate(dialogBinding, name, phone)) {
        if (existing == null) {
          viewModel.addContact(name, phone)
        } else {
          viewModel.updateContact(existing.id, name, phone)
        }
        dialog.dismiss()
      }
    }
  }

  private fun validate(binding: DialogContactBinding, name: String, phone: String): Boolean {
    var valid = true
    binding.nameLayout.error = null
    binding.phoneLayout.error = null
    if (name.isBlank()) {
      binding.nameLayout.error = getString(R.string.error_name_required)
      valid = false
    }
    if (phone.isBlank()) {
      binding.phoneLayout.error = getString(R.string.error_phone_required)
      valid = false
    } else if (!phone.matches(Regex("^[0-9+\\- ]+$"))) {
      binding.phoneLayout.error = getString(R.string.error_phone_invalid)
      valid = false
    }
    return valid
  }

  private fun confirmDelete(contact: Contact) {
    MaterialAlertDialogBuilder(this)
      .setTitle(getString(R.string.delete_confirm, contact.name))
      .setNegativeButton(R.string.cancel, null)
      .setPositiveButton(R.string.delete) { _, _ -> viewModel.deleteContact(contact.id) }
      .show()
  }
}
