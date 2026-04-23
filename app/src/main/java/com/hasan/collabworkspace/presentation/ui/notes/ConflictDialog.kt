package com.hasan.collabworkspace.presentation.ui.notes

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.hasan.collabworkspace.data.local.entities.NoteEntity

@Composable
fun ConflictDialog(
    local: NoteEntity,
    remote: NoteEntity,
    onResolve: (NoteEntity) -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Conflict Detected") },
        text = {
            Column {
                Text("Local: ${local.content}")
                Text("Remote: ${remote.content}")
            }
        },
        confirmButton = {
            Button(onClick = { onResolve(local) }) {
                Text("Keep Local")
            }
        },
        dismissButton = {
            Button(onClick = { onResolve(remote) }) {
                Text("Keep Remote")
            }
        }
    )
}

@Composable
fun NoteConflictDialog(
    local: NoteEntity,
    remote: NoteEntity,
    onResolve: (NoteEntity) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Note Conflict Detected") },
        text = {
            Column {
                Text("Local version: ${local.content}")
                Text("Remote version: ${remote.content}")
                Text("Local last modified: ${local.lastModified}")
                Text("Remote last modified: ${remote.lastModified}")
            }
        },
        confirmButton = {
            Button(onClick = { onResolve(local) }) {
                Text("Keep Local")
            }
        },
        dismissButton = {
            Button(onClick = { onResolve(remote) }) {
                Text("Keep Remote")
            }
        }
    )
}