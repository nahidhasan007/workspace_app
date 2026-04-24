package com.hasan.collabworkspace.domain.repository

import com.hasan.collabworkspace.domain.model.Asset
import com.hasan.collabworkspace.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface WorkspaceRepository {
    // Local Reads (Single Source of Truth)
    fun observeNotesByTab(tabId: String): Flow<List<Note>>
    fun observeAssetsByTab(tabId: String): Flow<List<Asset>>

    // Conflicts
    val noteConflicts: Flow<Pair<Note, Note>> // Local, Remote
    val assetConflicts: Flow<Pair<Asset, Asset>> // Local, Remote
    suspend fun resolveNoteConflict(resolvedNote: Note)
    suspend fun resolveAssetConflict(resolvedAsset: Asset)

    // Local Writes (Optimistic)
    suspend fun saveNote(note: Note)
    suspend fun deleteNote(noteId: String, timestamp: Long)
    suspend fun saveAsset(asset: Asset)
    suspend fun deleteAsset(assetId: String, timestamp: Long)

    // Remote Sync (Pull & Push triggers)
    suspend fun startListeningToRemoteChanges(tabId: String)
    suspend fun syncPendingChanges()
}