package com.hasan.collabworkspace.presentation.mvi

import com.hasan.collabworkspace.data.local.entities.NoteEntity
import com.hasan.collabworkspace.data.local.entities.AssetEntity

data class WorkspaceState(
    val notes: List<NoteEntity> = emptyList(),
    val assets: Map<String, List<AssetEntity>> = emptyMap(), // keyed by noteId
    val isLoading: Boolean = false,
    val error: String? = null,
    val conflictNotes: Pair<NoteEntity, NoteEntity>? = null,
    val conflictAssets: Pair<AssetEntity, AssetEntity>? = null
)