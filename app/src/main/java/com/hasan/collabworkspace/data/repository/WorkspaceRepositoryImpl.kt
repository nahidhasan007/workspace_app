package com.hasan.collabworkspace.data.repository

import com.hasan.collabworkspace.data.local.db.AssetDao
import com.hasan.collabworkspace.data.local.db.NoteDao
import com.hasan.collabworkspace.data.local.entities.AssetEntity
import com.hasan.collabworkspace.data.local.entities.NoteEntity
import com.hasan.collabworkspace.data.remote.FirestoreService
import com.hasan.collabworkspace.domain.repository.WorkspaceRepository
import kotlinx.coroutines.flow.Flow

class WorkspaceRepositoryImpl(
    private val noteDao: NoteDao,
    private val assetDao: AssetDao,
    private val firestore: FirestoreService
) : WorkspaceRepository {

    // Notes
    override fun getAllNotes(): Flow<List<NoteEntity>> = noteDao.getAllNotes()
    override fun getNoteById(id: String): Flow<NoteEntity?> = noteDao.getNoteById(id)

    override suspend fun upsertNote(note: NoteEntity) {
        noteDao.insertOrUpdate(note)
        firestore.pushNote(note) // push after local commit
    }

    override suspend fun deleteNote(id: String, soft: Boolean) {
        if (soft) noteDao.softDelete(id) else noteDao.hardDelete(id)
        // Firestore deletion handled via isDeleted flag
    }

    // Assets
    override fun getAssetsForNote(noteId: String): Flow<List<AssetEntity>> =
        assetDao.getAssetsForNote(noteId)

    override suspend fun upsertAsset(asset: AssetEntity) {
        assetDao.insertOrUpdate(asset)
        firestore.pushAsset(asset)
    }

    override suspend fun deleteAsset(id: String, soft: Boolean) {
        if (soft) assetDao.softDelete(id) else assetDao.hardDelete(id)
    }

    // Sync listeners
    override fun listenRemoteNotes(): Flow<List<NoteEntity>> = firestore.listenNotes()
    override fun listenRemoteAssets(): Flow<List<AssetEntity>> = firestore.listenAssets()

    override suspend fun pushNoteRemote(note: NoteEntity) = firestore.pushNote(note)
    override suspend fun pushAssetRemote(asset: AssetEntity) = firestore.pushAsset(asset)

    // Conflict resolution (stub for now, expanded in Step 10)
    override suspend fun resolveNoteConflict(local: NoteEntity, remote: NoteEntity): NoteEntity {
        return if (remote.lastModified > local.lastModified) remote else local
    }

    override suspend fun resolveAssetConflict(local: AssetEntity, remote: AssetEntity): AssetEntity {
        return if (remote.lastModified > local.lastModified) remote else local
    }
}