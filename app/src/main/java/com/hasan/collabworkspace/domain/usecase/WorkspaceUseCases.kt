package com.hasan.collabworkspace.domain.usecase

import com.hasan.collabworkspace.domain.model.Asset
import com.hasan.collabworkspace.domain.model.Note
import com.hasan.collabworkspace.domain.repository.WorkspaceRepository
import kotlinx.coroutines.flow.Flow

data class WorkspaceUseCases(
    val observeNotes: ObserveNotesUseCase,
    val observeAssetsByTab: ObserveAssetsByTabUseCase,
    val saveNote: SaveNoteUseCase,
    val saveAsset: SaveAssetUseCase,
    val deleteNote: DeleteNoteUseCase,
    val deleteAsset: DeleteAssetUseCase,
    val syncWorkspace: SyncWorkspaceUseCase,
    val resolveConflict: ResolveConflictUseCase
)

class ObserveNotesUseCase(private val repository: WorkspaceRepository) {
    operator fun invoke(tabId: String): Flow<List<Note>> = repository.observeNotesByTab(tabId)
}

class ObserveAssetsByTabUseCase(private val repository: WorkspaceRepository) {
    operator fun invoke(tabId: String): Flow<List<Asset>> = repository.observeAssetsByTab(tabId)
}

class SaveNoteUseCase(private val repository: WorkspaceRepository) {
    suspend operator fun invoke(note: Note) {
        val updatedNote = note.copy(
            lastModified = System.currentTimeMillis(),
            version = note.version + 1
        )
        repository.saveNote(updatedNote)
    }
}

class SaveAssetUseCase(private val repository: WorkspaceRepository) {
    suspend operator fun invoke(asset: Asset) {
        val updatedAsset = asset.copy(
            lastModified = System.currentTimeMillis(),
            version = asset.version + 1
        )
        repository.saveAsset(updatedAsset)
    }
}

class DeleteNoteUseCase(private val repository: WorkspaceRepository) {
    suspend operator fun invoke(noteId: String) {
        repository.deleteNote(noteId, System.currentTimeMillis())
    }
}

class DeleteAssetUseCase(private val repository: WorkspaceRepository) {
    suspend operator fun invoke(assetId: String) {
        repository.deleteAsset(assetId, System.currentTimeMillis())
    }
}

class SyncWorkspaceUseCase(private val repository: WorkspaceRepository) {
    suspend operator fun invoke(tabId: String) {
        repository.startListeningToRemoteChanges(tabId)
        repository.syncPendingChanges()
    }
}

class ResolveConflictUseCase(private val repository: WorkspaceRepository) {
    suspend fun resolveNote(resolvedNote: Note) {
        // Incrementing version ensures the resolved note overrides the remote one
        val updatedNote = resolvedNote.copy(
            lastModified = System.currentTimeMillis(),
            version = resolvedNote.version + 1
        )
        repository.resolveNoteConflict(updatedNote)
    }

    suspend fun resolveAsset(resolvedAsset: Asset) {
        val updatedAsset = resolvedAsset.copy(
            lastModified = System.currentTimeMillis(),
            version = resolvedAsset.version + 1
        )
        repository.resolveAssetConflict(updatedAsset)
    }
}
