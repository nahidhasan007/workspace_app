package com.hasan.collabworkspace.presentation.ui.assets

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.hasan.collabworkspace.data.local.entities.AssetEntity
import com.hasan.collabworkspace.data.local.entities.NoteEntity
import com.hasan.collabworkspace.presentation.mvi.WorkspaceState
import com.hasan.collabworkspace.presentation.mvi.WorkspaceViewModel

@Composable
fun AssetComposable(asset: AssetEntity, onTransform: (Float, Float, Float, Float) -> Unit) {
    var scale by remember { mutableStateOf(asset.scale) }
    var rotation by remember { mutableStateOf(asset.rotation) }
    var offsetX by remember { mutableStateOf(asset.posX) }
    var offsetY by remember { mutableStateOf(asset.posY) }

    Box(
        modifier = Modifier
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                rotationZ = rotation,
                translationX = offsetX,
                translationY = offsetY
            )
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, rot ->
                    scale *= zoom
                    rotation += rot
                    offsetX += pan.x
                    offsetY += pan.y
                    onTransform(offsetX, offsetY, rotation, scale)
                }
            }
    ) {
        Image(
            painter = rememberAsyncImagePainter(asset.uri),
            contentDescription = null,
            contentScale = ContentScale.Fit
        )
    }
}


@Composable
fun DraggableNote(
    note: NoteEntity,
    onMove: (Int) -> Unit
) {
    var offsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .offset(y = offsetY.dp)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetY += dragAmount.y
                    // Convert drag offset to new order index
                    val newOrder = (note.order + (offsetY / 100)).toInt().coerceAtLeast(0)
                    onMove(newOrder)
                }
            }
    ) {
        Text(note.content, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun DebugHUDOverlay(enabled: Boolean, state: WorkspaceState) {
    if (enabled) {
        Surface(
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column {
                Text("HUD Overlay", style = MaterialTheme.typography.titleMedium)
                Text("Notes: ${state.notes.size}")
                Text("Assets: ${state.assets.values.flatten().size}")
                Text("Conflicts: ${if (state.conflictNotes != null || state.conflictAssets != null) "Yes" else "No"}")
            }
        }
    }
}

@Composable
fun GestureHUDWrapper(viewModel: WorkspaceViewModel, content: @Composable () -> Unit) {
    var hudEnabled by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                onPress = { /* fallback single tap */ }
            )
            /*detectMultiFingerGestures { fingers ->
                if (fingers == 3) {
                    hudEnabled = !hudEnabled
                }
            }*/
        }
    ) {
        content()
        DebugHUDOverlay(enabled = hudEnabled, state = state)
    }
}

@Composable
fun AssetConflictDialog(
    local: AssetEntity,
    remote: AssetEntity,
    onResolve: (AssetEntity) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Asset Conflict Detected") },
        text = {
            Column {
                Text("Local position: (${local.posX}, ${local.posY}), rotation: ${local.rotation}, scale: ${local.scale}")
                Text("Remote position: (${remote.posX}, ${remote.posY}), rotation: ${remote.rotation}, scale: ${remote.scale}")
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