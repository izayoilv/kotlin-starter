package com.example.notesapp.view.editor

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notesapp.R
import com.example.notesapp.model.NotesError
import com.example.notesapp.viewmodel.NoteEditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
  viewModel: NoteEditorViewModel,
  onBack: () -> Unit,
  onSaved: () -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  var title by rememberSaveable { mutableStateOf("") }
  var body by rememberSaveable { mutableStateOf("") }
  val snackbarHostState = remember { SnackbarHostState() }
  val context = LocalContext.current

  LaunchedEffect(uiState.error) {
    val error = uiState.error ?: return@LaunchedEffect
    snackbarHostState.showSnackbar(context.getString(error.saveMessageRes()))
    viewModel.clearError()
  }

  LaunchedEffect(Unit) {
    viewModel.saved.collect { onSaved() }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(stringResource(R.string.add_note)) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
          }
        },
        actions = {
          TextButton(
            onClick = { viewModel.save(title, body) },
            enabled = !uiState.isSaving,
          ) {
            Text(stringResource(R.string.save))
          }
        },
      )
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
  ) { padding ->
    Column(
      modifier =
        Modifier
          .fillMaxSize()
          .padding(padding)
          .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      if (uiState.isSaving) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
      }
      OutlinedTextField(
        value = title,
        onValueChange = { title = it },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.title_hint)) },
        singleLine = true,
        isError = uiState.titleError,
        supportingText = {
          if (uiState.titleError) {
            Text(stringResource(R.string.error_title_required))
          }
        },
      )
      OutlinedTextField(
        value = body,
        onValueChange = { body = it },
        modifier =
          Modifier
            .fillMaxWidth()
            .weight(1f),
        label = { Text(stringResource(R.string.body_hint)) },
        minLines = 6,
      )
    }
  }
}

@StringRes
private fun NotesError.saveMessageRes(): Int =
  when (this) {
    NotesError.Network -> R.string.error_network
    else -> R.string.save_failed
  }
