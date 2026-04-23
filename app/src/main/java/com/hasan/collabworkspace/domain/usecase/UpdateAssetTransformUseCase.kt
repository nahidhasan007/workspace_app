package com.hasan.collabworkspace.domain.usecase

import com.hasan.collabworkspace.data.local.entities.AssetEntity
import com.hasan.collabworkspace.domain.repository.WorkspaceRepository

class UpdateAssetTransformUseCase(
    private val repository: WorkspaceRepository
) {
    suspend operator fun invoke(asset: AssetEntity, x: Float, y: Float, rotation: Float, scale: Float) {
        val updated = asset.copy(
            posX = x,
            posY = y,
            rotation = rotation,
            scale = scale,
            lastModified = System.currentTimeMillis(),
            version = asset.version + 1
        )
        repository.upsertAsset(updated)
    }
}