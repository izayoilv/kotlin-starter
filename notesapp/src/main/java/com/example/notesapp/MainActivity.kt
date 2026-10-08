package com.example.notesapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapp.databinding.ActivityMainBinding
import com.example.notesapp.databinding.DialogNoteEditorBinding
import com.example.notesapp.databinding.DialogNoteViewerBinding
import java.io.File

class MainActivity : AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding
  private lateinit var storage: NoteStorage
  private lateinit var prefs: PrefsManager
  private lateinit var adapter: NoteAdapter

  private val exportLauncher =
    registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
      if (uri != null) {
        try {
          contentResolver.openOutputStream(uri)?.use { stream ->
            stream.write(storage.exportJson().toByteArray())
          }
          Toast.makeText(this, R.string.export_success, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
          Toast.makeText(this, R.string.export_failed, Toast.LENGTH_SHORT).show()
        }
      }
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    storage = NoteStorage(File(filesDir, "notes"))
    prefs = PrefsManager(this)
    adapter = NoteAdapter(this, emptyList(), prefs.fontSize.sp)

    binding.noteList.adapter = adapter
    binding.addButton.setOnClickListener { showAddDialog() }
    binding.settingsButton.setOnClickListener {
      startActivity(Intent(this, SettingsActivity::class.java))
    }
    binding.exportButton.setOnClickListener {
      exportLauncher.launch("notes-export.json")
    }
    binding.noteList.setOnItemClickListener { _, _, position, _ ->
      showReadDialog(adapter.getItem(position))
    }
    binding.noteList.setOnItemLongClickListener { _, _, position, _ ->
      confirmDelete(adapter.getItem(position))
      true
    }
  }

  override fun onResume() {
    super.onResume()
    applyUserInfo()
    reload()
  }

  private fun applyUserInfo() {
    binding.userHeader.visibility = if (prefs.showUserInfo) View.VISIBLE else View.GONE
    val name = prefs.userName
    binding.userHeader.text =
      if (name.isBlank()) getString(R.string.default_greeting)
      else getString(R.string.greeting, name)
  }

  private fun reload() {
    adapter.submit(storage.listNotes(), prefs.fontSize.sp)
  }

  private fun showAddDialog() {
    val dialogBinding = DialogNoteEditorBinding.inflate(layoutInflater)
    val dialog = AlertDialog.Builder(this)
      .setTitle(R.string.add_note)
      .setView(dialogBinding.root)
      .setNegativeButton(R.string.cancel, null)
      .setPositiveButton(R.string.save, null)
      .create()

    dialog.show()
    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
      val title = dialogBinding.titleInput.text?.toString().orEmpty().trim()
      if (title.isBlank()) {
        dialogBinding.titleLayout.error = getString(R.string.error_title_required)
        return@setOnClickListener
      }
      val now = System.currentTimeMillis()
      storage.save(
        Note(
          id = now,
          title = title,
          body = dialogBinding.bodyInput.text?.toString().orEmpty(),
          author = prefs.userName.ifBlank { getString(R.string.default_author) },
          createdAt = now,
        ),
      )
      reload()
      dialog.dismiss()
    }
  }

  private fun showReadDialog(note: Note) {
    val viewer = DialogNoteViewerBinding.inflate(layoutInflater)
    viewer.viewerTitle.text = note.title
    viewer.viewerTitle.textSize = prefs.fontSize.sp
    viewer.viewerBody.text = note.body
    viewer.viewerBody.textSize = prefs.fontSize.sp
    AlertDialog.Builder(this)
      .setTitle(R.string.read_note)
      .setView(viewer.root)
      .setPositiveButton(R.string.ok, null)
      .show()
  }

  private fun confirmDelete(note: Note) {
    AlertDialog.Builder(this)
      .setTitle(getString(R.string.delete_confirm, note.title))
      .setNegativeButton(R.string.cancel, null)
      .setPositiveButton(R.string.delete) { _, _ ->
        storage.delete(note.id)
        reload()
      }
      .show()
  }
}
