package com.hasan.collabworkspace.presentation.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hasan.collabworkspace.domain.model.Asset
import com.hasan.collabworkspace.domain.model.Note
import com.hasan.collabworkspace.presentation.ui.theme.CollabworkspaceTheme
import com.hasan.collabworkspace.presentation.workspace.components.AssetBlock
import com.hasan.collabworkspace.presentation.workspace.components.NoteBlock
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceScreen(
    viewModel: WorkspaceViewModel
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workspace (${state.activeTabId})") },
                actions = {
                    IconButton(onClick = { 
                        viewModel.handleIntent(WorkspaceIntent.AddAsset(
                            Asset(id = UUID.randomUUID().toString(), tabId = state.activeTabId, imageUrl = "", x = 100f, y = 100f, rotation = 0f, scale = 1f, lastModified = System.currentTimeMillis(), version = 0)
                        )) 
                    }) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Add Image")
                    }
                    IconButton(onClick = { 
                        viewModel.handleIntent(WorkspaceIntent.AddNote(
                            Note(id = UUID.randomUUID().toString(), tabId = state.activeTabId, content = "New Note", orderIndex = (state.notes.size).toDouble(), lastModified = System.currentTimeMillis(), version = 0)
                        )) 
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Note")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown()
                        do {
                            val event = awaitPointerEvent()
                            if (event.changes.size >= 3) {
                                // 3-finger gesture detected
                                viewModel.handleIntent(WorkspaceIntent.ToggleDebugHud)
                                event.changes.forEach { it.consume() }
                            }
                        } while (event.changes.any { it.pressed })
                    }
                }
        ) {
            // Notion-style vertical document
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .zIndex(0f)
            ) {
                items(state.notes.sortedBy { it.orderIndex }, key = { it.id }) { note ->
                    NoteBlock(
                        note = note,
                        onContentChange = { newContent ->
                            viewModel.handleIntent(WorkspaceIntent.UpdateNoteContent(note.id, newContent))
                        },
                        onMoveUp = {
                            val currentIndex = state.notes.indexOf(note)
                            if (currentIndex > 0) {
                                val prevNote = state.notes[currentIndex - 1]
                                // Swap order indices
                                val tempIndex = note.orderIndex
                                viewModel.handleIntent(WorkspaceIntent.MoveNote(note.id, prevNote.orderIndex))
                                viewModel.handleIntent(WorkspaceIntent.MoveNote(prevNote.id, tempIndex))
                            }
                        },
                        onMoveDown = {
                            val currentIndex = state.notes.indexOf(note)
                            if (currentIndex < state.notes.size - 1) {
                                val nextNote = state.notes[currentIndex + 1]
                                val tempIndex = note.orderIndex
                                viewModel.handleIntent(WorkspaceIntent.MoveNote(note.id, nextNote.orderIndex))
                                viewModel.handleIntent(WorkspaceIntent.MoveNote(nextNote.id, tempIndex))
                            }
                        }
                    )
                }
            }

            // Figma-style absolute positioned assets (Overlay)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .zIndex(1f)
            ) {
                state.assets.forEach { asset ->
                    AssetBlock(
                        asset = asset,
                        onTransform = { x, y, rotation, scale ->
                            viewModel.handleIntent(
                                WorkspaceIntent.TransformAsset(
                                    asset.id,
                                    x,
                                    y,
                                    rotation,
                                    scale
                                )
                            )
                        }
                    )
                }
            }

            // Debug HUD
            if (state.showDebugHud) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(8.dp)
                ) {
                    Text(
                        "DEBUG HUD\nActive Tab: ${state.activeTabId}\nNotes: ${state.notes.size}\nAssets: ${state.assets.size}",
                        color = Color.White
                    )
                }
            }

            // Conflict Dialogs
            state.pendingNoteConflict?.let { (local, remote) ->
                ConflictDialog(
                    title = "Note Conflict",
                    localContent = local.content,
                    remoteContent = remote.content,
                    onKeepLocal = { viewModel.handleIntent(WorkspaceIntent.ResolveNoteConflict(local)) },
                    onKeepRemote = { viewModel.handleIntent(WorkspaceIntent.ResolveNoteConflict(remote)) }
                )
            }

            state.pendingAssetConflict?.let { (local, remote) ->
                ConflictDialog(
                    title = "Asset Conflict",
                    localContent = "X: ${local.x}, Y: ${local.y}",
                    remoteContent = "X: ${remote.x}, Y: ${remote.y}",
                    onKeepLocal = { viewModel.handleIntent(WorkspaceIntent.ResolveAssetConflict(local)) },
                    onKeepRemote = { viewModel.handleIntent(WorkspaceIntent.ResolveAssetConflict(remote)) }
                )
            }
        }
    }
}

@Composable
fun ConflictDialog(
    title: String,
    localContent: String,
    remoteContent: String,
    onKeepLocal: () -> Unit,
    onKeepRemote: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* Must explicitly resolve */ },
        title = { Text(title) },
        text = {
            Column {
                Text("Your version:\n$localContent", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Remote version:\n$remoteContent", style = MaterialTheme.typography.bodyMedium)
            }
        },
        confirmButton = {
            Button(onClick = onKeepLocal) { Text("Keep Mine") }
        },
        dismissButton = {
            Button(onClick = onKeepRemote) { Text("Keep Remote") }
        }
    )
}
