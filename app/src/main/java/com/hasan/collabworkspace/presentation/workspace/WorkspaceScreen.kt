package com.hasan.collabworkspace.presentation.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hasan.collabworkspace.domain.model.Asset
import com.hasan.collabworkspace.domain.model.Note
import com.hasan.collabworkspace.presentation.workspace.components.AssetBlock
import com.hasan.collabworkspace.presentation.workspace.components.NoteBlock
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceScreen(
    viewModel: WorkspaceViewModel
) {
    val state by viewModel.state.collectAsState()
    val tabs = listOf("default_tab", "project_x", "brainstorm")
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 4.dp
            ) {
                Column {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Collab Workspace",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                if (state.pendingNoteConflict != null || state.pendingAssetConflict != null) {
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError
                                    ) {
                                        Text(
                                            "CONFLICT",
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        )
                                    }
                                }
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                viewModel.handleIntent(WorkspaceIntent.AddAsset(
                                    Asset(
                                        id = UUID.randomUUID().toString(),
                                        tabId = state.activeTabId,
                                        imageUrl = "",
                                        x = 100f,
                                        y = 100f,
                                        rotation = 0f,
                                        scale = 1f,
                                        lastModified = System.currentTimeMillis(),
                                        version = 0
                                    )
                                ))
                            }) {
                                Icon(
                                    Icons.Default.AddCircle,
                                    contentDescription = "Add Image",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(onClick = {
                                val now = System.currentTimeMillis()
                                viewModel.handleIntent(WorkspaceIntent.AddNote(
                                    Note(
                                        id = UUID.randomUUID().toString(),
                                        tabId = state.activeTabId,
                                        content = "",
                                        orderIndex = (state.notes.size).toDouble(),
                                        createdAt = now,
                                        lastModified = now,
                                        version = 0
                                    )
                                ))
                            }) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add Note",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                    ScrollableTabRow(
                        selectedTabIndex = tabs.indexOf(state.activeTabId).coerceAtLeast(0),
                        edgePadding = 16.dp,
                        containerColor = Color.Transparent,
                        divider = {},
                        indicator = { tabPositions ->
                            if (tabs.indexOf(state.activeTabId) >= 0) {
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[tabs.indexOf(state.activeTabId)]),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    ) {
                        tabs.forEach { tab ->
                            val selected = state.activeTabId == tab
                            Tab(
                                selected = selected,
                                onClick = { viewModel.handleIntent(WorkspaceIntent.SwitchTab(tab)) },
                                text = {
                                    Text(
                                        tab.replaceFirstChar { it.uppercase() },
                                        style = if (selected) MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                        else MaterialTheme.typography.labelLarge
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown()
                        do {
                            val event = awaitPointerEvent()
                            if (event.changes.size >= 3) {
                                viewModel.handleIntent(WorkspaceIntent.ToggleDebugHud)
                                event.changes.forEach { it.consume() }
                            }
                        } while (event.changes.any { it.pressed })
                    }
                }
        ) {
            // Tiles/Grid View for Notes
            // Organizing by orderIndex (Manual) or createdAt (Default)
            val sortedNotes = remember(state.notes) { 
                state.notes.sortedWith(compareBy<Note> { it.orderIndex }.thenBy { it.createdAt })
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sortedNotes, key = { it.id }) { note ->
                    val index = sortedNotes.indexOf(note)
                    NoteBlock(
                        note = note,
                        onContentChange = { newContent ->
                            viewModel.handleIntent(WorkspaceIntent.UpdateNoteContent(note.id, newContent))
                        },
                        onDelete = {
                            viewModel.handleIntent(WorkspaceIntent.DeleteNote(note.id))
                        },
                        onMoveUp = {
                            if (index > 0) {
                                val prevNote = sortedNotes[index - 1]
                                val prevPrevIndex = if (index > 1) sortedNotes[index - 2].orderIndex else prevNote.orderIndex - 1.0
                                viewModel.handleIntent(WorkspaceIntent.MoveNote(note.id, (prevNote.orderIndex + prevPrevIndex) / 2.0))
                            }
                        },
                        onMoveDown = {
                            if (index < sortedNotes.size - 1) {
                                val nextNote = sortedNotes[index + 1]
                                val nextNextIndex = if (index < sortedNotes.size - 2) sortedNotes[index + 2].orderIndex else nextNote.orderIndex + 1.0
                                viewModel.handleIntent(WorkspaceIntent.MoveNote(note.id, (nextNote.orderIndex + nextNextIndex) / 2.0))
                            }
                        }
                    )
                }
            }

            // Floating Assets
            state.assets.forEach { asset ->
                AssetBlock(
                    asset = asset,
                    onTransform = { x, y, rotation, scale ->
                        viewModel.handleIntent(WorkspaceIntent.TransformAsset(asset.id, x, y, rotation, scale))
                    },
                    onDelete = {
                        viewModel.handleIntent(WorkspaceIntent.DeleteAsset(asset.id))
                    }
                )
            }

            // Debug HUD
            if (state.showDebugHud) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    color = Color.Black.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "DEBUG: Tab=${state.activeTabId} | Notes=${state.notes.size} | Assets=${state.assets.size}",
                        color = Color.White,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Conflict Dialogs
            state.pendingNoteConflict?.let { (local, remote) ->
                ConflictDialog(
                    title = "Note Conflict Detected",
                    localContent = local.content,
                    remoteContent = remote.content,
                    onKeepLocal = { viewModel.handleIntent(WorkspaceIntent.ResolveNoteConflict(local)) },
                    onKeepRemote = { viewModel.handleIntent(WorkspaceIntent.ResolveNoteConflict(remote)) }
                )
            }

            state.pendingAssetConflict?.let { (local, remote) ->
                ConflictDialog(
                    title = "Asset Conflict Detected",
                    localContent = "Pos: (${local.x.toInt()}, ${local.y.toInt()}) Rot: ${local.rotation.toInt()}°",
                    remoteContent = "Pos: (${remote.x.toInt()}, ${remote.y.toInt()}) Rot: ${remote.rotation.toInt()}°",
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
        onDismissRequest = { },
        title = { Text(title) },
        text = {
            Column {
                Text("Conflict found while you were offline. Choose which version to keep:", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(16.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Your Local Version:", style = MaterialTheme.typography.labelMedium)
                        Text(localContent)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Remote Cloud Version:", style = MaterialTheme.typography.labelMedium)
                        Text(remoteContent)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onKeepLocal) { Text("Keep Local") }
        },
        dismissButton = {
            TextButton(onClick = onKeepRemote) { Text("Accept Remote") }
        }
    )
}
