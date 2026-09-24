package com.example.contact

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ContactViewModelTest {
  @get:Rule
  val instantTaskExecutorRule = InstantTaskExecutorRule()

  @Test
  fun `initial list is empty`() {
    assertTrue(ContactViewModel().contacts.value.orEmpty().isEmpty())
  }

  @Test
  fun `addContact appends with incrementing ids`() {
    val viewModel = ContactViewModel()
    viewModel.addContact("Ada", "111")
    viewModel.addContact("Grace", "222")

    val contacts = viewModel.contacts.value.orEmpty()
    assertEquals(2, contacts.size)
    assertEquals(1L, contacts[0].id)
    assertEquals(2L, contacts[1].id)
    assertEquals("Ada", contacts[0].name)
    assertEquals("111", contacts[0].phone)
  }

  @Test
  fun `updateContact replaces name and phone`() {
    val viewModel = ContactViewModel()
    viewModel.addContact("Ada", "111")

    viewModel.updateContact(1L, "Ada Lovelace", "999")

    val contact = viewModel.contacts.value.orEmpty().single()
    assertEquals("Ada Lovelace", contact.name)
    assertEquals("999", contact.phone)
  }

  @Test
  fun `updateContact keeps other contacts intact`() {
    val viewModel = ContactViewModel()
    viewModel.addContact("Ada", "111")
    viewModel.addContact("Grace", "222")

    viewModel.updateContact(1L, "Ada L", "999")

    val contacts = viewModel.contacts.value.orEmpty()
    assertEquals(2, contacts.size)
    assertEquals("Grace", contacts[1].name)
    assertEquals("222", contacts[1].phone)
  }

  @Test
  fun `deleteContact removes only the matching id`() {
    val viewModel = ContactViewModel()
    viewModel.addContact("Ada", "111")
    viewModel.addContact("Grace", "222")

    viewModel.deleteContact(1L)

    val contacts = viewModel.contacts.value.orEmpty()
    assertEquals(1, contacts.size)
    assertEquals("Grace", contacts.first().name)
  }
}
