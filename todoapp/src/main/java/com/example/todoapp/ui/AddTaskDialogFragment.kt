package com.example.todoapp.ui

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import com.example.todoapp.R
import com.example.todoapp.databinding.DialogAddTaskBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddTaskDialogFragment : DialogFragment() {
  private var binding: DialogAddTaskBinding? = null
  private var draftTitle: String? = null

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val viewBinding = DialogAddTaskBinding.inflate(layoutInflater)
    binding = viewBinding
    draftTitle = savedInstanceState?.getString(KEY_DRAFT)
    viewBinding.etTitle.setText(draftTitle)
    viewBinding.etTitle.doOnTextChanged { text, _, _, _ ->
      draftTitle = text?.toString()
      viewBinding.tilTitle.error = null
    }

    return MaterialAlertDialogBuilder(requireContext())
      .setTitle(R.string.add_task)
      .setView(viewBinding.root)
      .setPositiveButton(R.string.save, null)
      .setNegativeButton(R.string.cancel, null)
      .create()
      .apply {
        setOnShowListener {
          getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val title = viewBinding.etTitle.text?.toString()?.trim().orEmpty()
            if (title.isEmpty()) {
              viewBinding.tilTitle.error = getString(R.string.error_title_required)
            } else {
              parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                Bundle().apply { putString(KEY_TITLE, title) },
              )
              dismiss()
            }
          }
        }
      }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putString(KEY_DRAFT, draftTitle)
  }

  override fun onDestroyView() {
    binding = null
    super.onDestroyView()
  }

  companion object {
    const val REQUEST_KEY = "add_task"
    const val KEY_TITLE = "title"
    const val KEY_DRAFT = "draft"
    const val TAG = "AddTaskDialogFragment"
  }
}
