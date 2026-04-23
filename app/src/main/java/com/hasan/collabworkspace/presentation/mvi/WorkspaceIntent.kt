package com.hasan.collabworkspace.presentation.mvi

sealed class WorkspaceIntent {
    // Notes
    data class CreateNote(val content: String, val order: Int) : WorkspaceIntent()
    data class UpdateNote(val noteId: String, val newContent: String) : WorkspaceIntent()
    data class DeleteNote(val noteId: String, val soft: Boolean = true) : WorkspaceIntent()

    // Assets
    data class AddAsset(val noteId: String, val uri: String) : WorkspaceIntent()
    data class UpdateAssetTransform(
        val assetId: String,
        val x: Float,
        val y: Float,
        val rotation: Float,
        val scale: Float
    ) : WorkspaceIntent()
    data class DeleteAsset(val assetId: String, val soft: Boolean = true) : WorkspaceIntent()

    // Sync
    object SyncNotes : WorkspaceIntent()
    object SyncAssets : WorkspaceIntent()

    // Conflict
    data class ResolveNoteConflict(val localId: String, val remoteId: String) : WorkspaceIntent()
    data class ResolveAssetConflict(val localId: String, val remoteId: String) : WorkspaceIntent()
}