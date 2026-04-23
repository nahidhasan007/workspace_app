package com.hasan.collabworkspace.domain.usecase

import com.hasan.collabworkspace.data.local.entities.NoteEntity
import com.hasan.collabworkspace.data.local.entities.AssetEntity
import com.hasan.collabworkspace.domain.repository.WorkspaceRepository

class ResolveConflictUseCase(
    private val repository: WorkspaceRepository
) {
    suspend fun resolveNote(local: NoteEntity, remote: NoteEntity): NoteEntity {
        return repository.resolveNoteConflict(local, remote)
    }

    suspend fun resolveAsset(local: AssetEntity, remote: AssetEntity): AssetEntity {
        return repository.resolveAssetConflict(local, remote)
    }
}