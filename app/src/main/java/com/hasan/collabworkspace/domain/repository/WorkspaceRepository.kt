package com.hasan.collabworkspace.domain.repository

import com.hasan.collabworkspace.data.local.entities.NoteEntity
import com.hasan.collabworkspace.data.local.entities.AssetEntity
import kotlinx.coroutines.flow.Flow

interface WorkspaceRepository {

    // Notes
    fun getAllNotes(): Flow<List<NoteEntity>>
    fun getNoteById(id: String): Flow<NoteEntity?>
    suspend fun upsertNote(note: NoteEntity)
    suspend fun deleteNote(id: String, soft: Boolean = true)

    // Assets
    fun getAssetsForNote(noteId: String): Flow<List<AssetEntity>>
    suspend fun upsertAsset(asset: AssetEntity)
    suspend fun deleteAsset(id: String, soft: Boolean = true)

    // Sync
    fun listenRemoteNotes(): Flow<List<NoteEntity>>
    fun listenRemoteAssets(): Flow<List<AssetEntity>>
    suspend fun pushNoteRemote(note: NoteEntity)
    suspend fun pushAssetRemote(asset: AssetEntity)

    // Conflict resolution
    suspend fun resolveNoteConflict(local: NoteEntity, remote: NoteEntity): NoteEntity
    suspend fun resolveAssetConflict(local: AssetEntity, remote: AssetEntity): AssetEntity
}