package com.example.notesapp.view.detail

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notesapp.R
import com.example.notesapp.model.NotesError
import com.example.notesapp.view.formatCreatedAt
import com.example.notesapp.viewmodel.NoteDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
  viewModel: NoteDetailViewModel,
  noteId: Long,
  onBack: () -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }
  var showDeleteDialog by remember { mutableStateOf(false) }
  val context = LocalContext.current
  val note = uiState.note

  LaunchedEffect(noteId) {
    viewModel.load(noteId)
  }

  LaunchedEffect(Unit) {
    viewModel.deleted.collect { onBack() }
  }

  LaunchedEffect(uiState.error) {
    val error = uiState.error ?: return@LaunchedEffect
    snackbarHostState.showSnackbar(context.getString(error.deleteMessageRes()))
    viewModel.clearError()
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(stringResource(R.string.notes_title)) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
          }
        },
        actions = {
          if (note != null) {
            IconButton(
              onClick = { showDeleteDialog = true },
              enabled = !uiState.isDeleting,
            ) {
              Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete))
            }
          }
        },
      )
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
  ) { padding ->
    when {
      uiState.isLoading -> {
        Box(
          modifier = Modifier.fillMaxSize().padding(padding),
          contentAlignment = Alignment.Center,
        ) {
          CircularProgressIndicator()
        }
      }

      note == null -> {
        Box(
          modifier = Modifier.fillMaxSize().padding(padding),
          contentAlignment = Alignment.Center,
        ) {
          Text(stringResource(R.string.note_not_found))
        }
      }

      else -> {
        Column(
          modifier =
            Modifier
              .fillMaxSize()
              .padding(padding)
              .verticalScroll(rememberScrollState())
              .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          Text(
            text = note.title,
            style = MaterialTheme.typography.headlineSmall,
          )
          Text(
            text = formatCreatedAt(note.createdAt),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Text(
            text = note.body,
            style = MaterialTheme.typography.bodyLarge,
          )
        }
      }
    }
  }

  val noteToDelete = uiState.note
  if (showDeleteDialog && noteToDelete != null) {
    AlertDialog(
      onDismissRequest = { showDeleteDialog = false },
      title = { Text(stringResource(R.string.delete)) },
      text = { Text(stringResource(R.string.delete_confirm, noteToDelete.title)) },
      confirmButton = {
        TextButton(
          onClick = {
            viewModel.delete()
            showDeleteDialog = false
          },
        ) {
          Text(stringResource(R.string.delete))
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteDialog = false }) {
          Text(stringResource(R.string.cancel))
        }
      },
    )
  }
}

@StringRes
private fun NotesError.deleteMessageRes(): Int =
  when (this) {
    NotesError.Network -> R.string.error_network
    else -> R.string.delete_failed
  }
