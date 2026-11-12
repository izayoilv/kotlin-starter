package com.example.notesapp.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.notesapp.view.notes.NotesListScreen
import com.example.notesapp.viewmodel.NotesViewModel
import kotlinx.serialization.Serializable

@Serializable
data object NotesList : NavKey

@Serializable
data class NoteDetail(
  val noteId: Long,
) : NavKey

@Serializable
data object NoteEditor : NavKey

@Composable
fun NotesApp() {
  val backStack = rememberNavBackStack(NotesList)

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryDecorators =
      listOf(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator(),
      ),
    entryProvider =
      entryProvider {
        entry<NotesList> {
          val viewModel: NotesViewModel = hiltViewModel()
          NotesListScreen(
            viewModel = viewModel,
            onAdd = {},
            onNoteClick = {},
          )
        }
      },
  )
}
