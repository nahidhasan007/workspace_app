package com.hasan.collabworkspace.data.repository

import com.hasan.collabworkspace.data.local.dao.WorkspaceDao
import com.hasan.collabworkspace.data.mapper.toDomain
import com.hasan.collabworkspace.data.mapper.toDto
import com.hasan.collabworkspace.data.mapper.toEntity
import com.hasan.collabworkspace.data.remote.FirestoreDataSource
import com.hasan.collabworkspace.domain.model.Asset
import com.hasan.collabworkspace.domain.model.Note
import com.hasan.collabworkspace.domain.repository.WorkspaceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class WorkspaceRepositoryImpl(
    private val dao: WorkspaceDao,
    private val remote: FirestoreDataSource?
) : WorkspaceRepository {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _noteConflicts = kotlinx.coroutines.flow.MutableSharedFlow<Pair<Note, Note>>()
    override val noteConflicts: Flow<Pair<Note, Note>> = _noteConflicts

    private val _assetConflicts = kotlinx.coroutines.flow.MutableSharedFlow<Pair<Asset, Asset>>()
    override val assetConflicts: Flow<Pair<Asset, Asset>> = _assetConflicts

    // --- Local Reads ---
    override fun observeNotesByTab(tabId: String): Flow<List<Note>> {
        return dao.observeNotesByTab(tabId).map { entities -> 
            entities.map { it.toDomain() } 
        }
    }

    override fun observeAssetsByTab(tabId: String): Flow<List<Asset>> {
        return dao.observeAssetsByTab(tabId).map { entities -> 
            entities.map { it.toDomain() } 
        }
    }

    // --- Local Writes ---
    override suspend fun saveNote(note: Note) {
        val entity = note.toEntity(isPendingSync = true)
        dao.insertOrUpdateNote(entity)
        pushNoteSafely(entity.id)
    }

    override suspend fun deleteNote(noteId: String, timestamp: Long) {
        dao.softDeleteNote(noteId, timestamp)
        pushNoteSafely(noteId)
    }

    override suspend fun saveAsset(asset: Asset) {
        val entity = asset.toEntity(isPendingSync = true)
        dao.insertOrUpdateAsset(entity)
        pushAssetSafely(entity.id)
    }

    override suspend fun deleteAsset(assetId: String, timestamp: Long) {
        dao.softDeleteAsset(assetId, timestamp)
        pushAssetSafely(assetId)
    }

    // --- Remote Sync ---
    override suspend fun startListeningToRemoteChanges(tabId: String) {
        scope.launch {
            try {
                remote?.observeNotes(tabId)?.collect { remoteNotes ->
                    remoteNotes.forEach { dto ->
                        val local = dao.getNoteById(dto.id)
                        if (local == null) {
                            dao.insertOrUpdateNote(dto.toEntity(isPendingSync = false))
                        } else if (local.isPendingSync && dto.version > local.version) {
                            // Conflict detected!
                            _noteConflicts.emit(Pair(local.toDomain(), dto.toEntity(false).toDomain()))
                        } else if (dto.version > local.version || (dto.version == local.version && dto.lastModified > local.lastModified)) {
                            dao.insertOrUpdateNote(dto.toEntity(isPendingSync = false))
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignore remote errors to keep local functionality working
                e.printStackTrace()
            }
        }
        
        scope.launch {
            try {
                remote?.observeAssets(tabId)?.collect { remoteAssets ->
                    remoteAssets.forEach { dto ->
                        val local = dao.getAssetById(dto.id)
                        if (local == null) {
                            dao.insertOrUpdateAsset(dto.toEntity(isPendingSync = false))
                        } else if (local.isPendingSync && dto.version > local.version) {
                            // Conflict detected!
                            _assetConflicts.emit(Pair(local.toDomain(), dto.toEntity(false).toDomain()))
                        } else if (dto.version > local.version || (dto.version == local.version && dto.lastModified > local.lastModified)) {
                            dao.insertOrUpdateAsset(dto.toEntity(isPendingSync = false))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun syncPendingChanges() {
        val pendingNotes = dao.getPendingNotes()
        pendingNotes.forEach { entity ->
            try {
                remote?.pushNote(entity.toDto())
                if (remote != null) dao.clearNotePendingSync(entity.id)
            } catch (e: Exception) {
                // Keep pending if failed
            }
        }

        val pendingAssets = dao.getPendingAssets()
        pendingAssets.forEach { entity ->
            try {
                remote?.pushAsset(entity.toDto())
                if (remote != null) dao.clearAssetPendingSync(entity.id)
            } catch (e: Exception) {
                // Keep pending if failed
            }
        }
    }

    override suspend fun resolveNoteConflict(resolvedNote: Note) {
        // Saving the resolved note increments version (done in UI Intent before calling this)
        saveNote(resolvedNote)
    }

    override suspend fun resolveAssetConflict(resolvedAsset: Asset) {
        saveAsset(resolvedAsset)
    }

    // --- Helpers ---
    private fun pushNoteSafely(noteId: String) {
        scope.launch {
            val entity = dao.getNoteById(noteId) ?: return@launch
            if (entity.isPendingSync) {
                try {
                    remote?.pushNote(entity.toDto())
                    if (remote != null) dao.clearNotePendingSync(noteId)
                } catch (e: Exception) {
                    // SyncWorker will pick it up later if this fails
                }
            }
        }
    }

    private fun pushAssetSafely(assetId: String) {
        scope.launch {
            val entity = dao.getAssetById(assetId) ?: return@launch
            if (entity.isPendingSync) {
                try {
                    remote?.pushAsset(entity.toDto())
                    if (remote != null) dao.clearAssetPendingSync(assetId)
                } catch (e: Exception) {
                    // SyncWorker will pick it up later if this fails
                }
            }
        }
    }
}