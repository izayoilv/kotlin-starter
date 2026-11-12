package com.example.notesapp.view.notes

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notesapp.R
import com.example.notesapp.model.Note
import com.example.notesapp.model.NotesError
import com.example.notesapp.view.formatCreatedAt
import com.example.notesapp.viewmodel.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NotesListScreen(
  viewModel: NotesViewModel,
  onAdd: () -> Unit,
  onNoteClick: (Long) -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }
  var pendingDelete by remember { mutableStateOf<Note?>(null) }
  val retryLabel = stringResource(R.string.retry)
  val context = LocalContext.current

  LaunchedEffect(uiState.error) {
    val error = uiState.error ?: return@LaunchedEffect
    val result =
      snackbarHostState.showSnackbar(
        message = context.getString(error.messageRes()),
        actionLabel = retryLabel,
      )
    if (result == SnackbarResult.ActionPerformed) {
      viewModel.refresh()
    }
    viewModel.clearError()
  }

  Scaffold(
    topBar = {
      TopAppBar(title = { Text(stringResource(R.string.notes_title)) })
    },
    floatingActionButton = {
      FloatingActionButton(onClick = onAdd) {
        Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_note))
      }
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
  ) { padding ->
    PullToRefreshBox(
      isRefreshing = uiState.isRefreshing,
      onRefresh = { viewModel.refresh() },
      modifier = Modifier.fillMaxSize().padding(padding),
    ) {
      if (uiState.notes.isEmpty() && !uiState.isRefreshing) {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center,
        ) {
          Text(stringResource(R.string.empty_notes))
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          items(uiState.notes, key = { it.id }) { note ->
            NoteCard(
              note = note,
              onClick = { onNoteClick(note.id) },
              onLongClick = { pendingDelete = note },
            )
          }
        }
      }
    }
  }

  pendingDelete?.let { note ->
    AlertDialog(
      onDismissRequest = { pendingDelete = null },
      title = { Text(stringResource(R.string.delete)) },
      text = { Text(stringResource(R.string.delete_confirm, note.title)) },
      confirmButton = {
        TextButton(
          onClick = {
            viewModel.delete(note.id)
            pendingDelete = null
          },
        ) {
          Text(stringResource(R.string.delete))
        }
      },
      dismissButton = {
        TextButton(onClick = { pendingDelete = null }) {
          Text(stringResource(R.string.cancel))
        }
      },
    )
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteCard(
  note: Note,
  onClick: () -> Unit,
  onLongClick: () -> Unit,
) {
  Card(
    modifier =
      Modifier
        .fillMaxWidth()
        .combinedClickable(onClick = onClick, onLongClick = onLongClick),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = note.title,
        style = MaterialTheme.typography.titleMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      if (note.body.isNotBlank()) {
        Text(
          text = note.body,
          style = MaterialTheme.typography.bodyMedium,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )
      }
      Text(
        text = formatCreatedAt(note.createdAt),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 8.dp),
      )
    }
  }
}

@StringRes
private fun NotesError.messageRes(): Int =
  when (this) {
    NotesError.Network -> R.string.error_network
    else -> R.string.error_generic
  }
