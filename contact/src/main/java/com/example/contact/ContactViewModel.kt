package com.example.contact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ContactViewModel : ViewModel() {
  private val _contacts = MutableLiveData<List<Contact>>(emptyList())
  val contacts: LiveData<List<Contact>> = _contacts

  private var nextId = 1L

  fun addContact(name: String, phone: String) {
    val contact = Contact(id = nextId++, name = name, phone = phone)
    _contacts.value = _contacts.value.orEmpty() + contact
  }

  fun updateContact(id: Long, name: String, phone: String) {
    _contacts.value = _contacts.value.orEmpty().map {
      if (it.id == id) it.copy(name = name, phone = phone) else it
    }
  }

  fun deleteContact(id: Long) {
    _contacts.value = _contacts.value.orEmpty().filterNot { it.id == id }
  }
}
