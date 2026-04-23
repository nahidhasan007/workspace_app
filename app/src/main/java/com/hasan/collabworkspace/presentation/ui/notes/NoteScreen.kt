package com.hasan.collabworkspace.presentation.ui.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hasan.collabworkspace.presentation.mvi.WorkspaceViewModel
import com.hasan.collabworkspace.presentation.mvi.WorkspaceIntent
import com.hasan.collabworkspace.data.local.entities.NoteEntity

@Composable
fun NoteScreen(viewModel: WorkspaceViewModel) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Notes", style = MaterialTheme.typography.titleLarge)

        LazyColumn {
            items(state.notes) { note ->
                NoteItem(note = note, onUpdate = { newContent ->
                    viewModel.processIntent(
                        WorkspaceIntent.UpdateNote(note.id, newContent)
                    )
                }, onDelete = {
                    viewModel.processIntent(
                        WorkspaceIntent.DeleteNote(note.id)
                    )
                })
            }
        }
    }

    state.conflictNotes?.let { conflict ->
        ConflictDialog(
            local = conflict.first,
            remote = conflict.second,
            onResolve = { chosen ->
                viewModel.processIntent(
                    WorkspaceIntent.ResolveNoteConflict(conflict.first.id, conflict.second.id)
                )
            }
        )
    }
}