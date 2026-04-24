package com.hasan.collabworkspace.presentation.workspace

import com.hasan.collabworkspace.domain.model.Asset
import com.hasan.collabworkspace.domain.model.Note

data class WorkspaceState(
    val activeTabId: String = "default_tab", // For multi-tab support
    val notes: List<Note> = emptyList(),
    val assets: List<Asset> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    
    // Conflict resolution state
    val pendingNoteConflict: Pair<Note, Note>? = null, // Local, Remote
    val pendingAssetConflict: Pair<Asset, Asset>? = null, // Local, Remote

    // Debug HUD state (3-finger gesture)
    val showDebugHud: Boolean = false
)

sealed class WorkspaceIntent {
    // Tab operations
    data class SwitchTab(val tabId: String) : WorkspaceIntent()

    // Note operations
    data class AddNote(val note: Note) : WorkspaceIntent()
    data class UpdateNoteContent(val noteId: String, val content: String) : WorkspaceIntent()
    data class MoveNote(val noteId: String, val newOrderIndex: Double) : WorkspaceIntent()
    data class DeleteNote(val noteId: String) : WorkspaceIntent()

    // Asset operations
    data class AddAsset(val asset: Asset) : WorkspaceIntent()
    data class TransformAsset(val assetId: String, val x: Float, val y: Float, val rotation: Float, val scale: Float) : WorkspaceIntent()
    data class DeleteAsset(val assetId: String) : WorkspaceIntent()

    // Conflict Resolution
    data class ResolveNoteConflict(val resolvedNote: Note) : WorkspaceIntent()
    data class ResolveAssetConflict(val resolvedAsset: Asset) : WorkspaceIntent()

    // Gestures
    object ToggleDebugHud : WorkspaceIntent()
}
