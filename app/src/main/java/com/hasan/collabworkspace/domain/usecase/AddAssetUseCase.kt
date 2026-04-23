package com.hasan.collabworkspace.domain.usecase

import com.hasan.collabworkspace.data.local.entities.AssetEntity
import com.hasan.collabworkspace.domain.repository.WorkspaceRepository

class AddAssetUseCase(
    private val repository: WorkspaceRepository
) {
    suspend operator fun invoke(noteId: String, uri: String) {
        val asset = AssetEntity(
            noteId = noteId,
            uri = uri,
            posX = 0f,
            posY = 0f,
            rotation = 0f,
            scale = 1f,
            lastModified = System.currentTimeMillis(),
            version = 1,
            isDeleted = false
        )
        repository.upsertAsset(asset)
    }
}