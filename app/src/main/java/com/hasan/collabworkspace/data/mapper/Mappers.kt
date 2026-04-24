package com.hasan.collabworkspace.data.mapper

import com.hasan.collabworkspace.data.local.entity.AssetEntity
import com.hasan.collabworkspace.data.local.entity.NoteEntity
import com.hasan.collabworkspace.data.remote.dto.AssetDto
import com.hasan.collabworkspace.data.remote.dto.NoteDto
import com.hasan.collabworkspace.domain.model.Asset
import com.hasan.collabworkspace.domain.model.Note

// --- Note Mappers ---

fun NoteEntity.toDomain(): Note {
    return Note(
        id = id,
        tabId = tabId,
        content = content,
        orderIndex = orderIndex,
        createdAt = createdAt,
        lastModified = lastModified,
        version = version
    )
}

fun Note.toEntity(isPendingSync: Boolean = true): NoteEntity {
    return NoteEntity(
        id = id,
        tabId = tabId,
        content = content,
        orderIndex = orderIndex,
        createdAt = createdAt,
        lastModified = lastModified,
        version = version,
        isDeleted = false,
        isPendingSync = isPendingSync
    )
}

fun NoteDto.toEntity(isPendingSync: Boolean = false): NoteEntity {
    return NoteEntity(
        id = id,
        tabId = tabId,
        content = content,
        orderIndex = orderIndex,
        createdAt = createdAt,
        lastModified = lastModified,
        version = version,
        isDeleted = isDeleted,
        isPendingSync = isPendingSync
    )
}

fun NoteEntity.toDto(): NoteDto {
    return NoteDto(
        id = id,
        tabId = tabId,
        content = content,
        orderIndex = orderIndex,
        createdAt = createdAt,
        lastModified = lastModified,
        version = version,
        isDeleted = isDeleted
    )
}

// --- Asset Mappers ---

fun AssetEntity.toDomain(): Asset {
    return Asset(
        id = id,
        tabId = tabId,
        imageUrl = imageUrl,
        x = x,
        y = y,
        rotation = rotation,
        scale = scale,
        lastModified = lastModified,
        version = version
    )
}

fun Asset.toEntity(isPendingSync: Boolean = true): AssetEntity {
    return AssetEntity(
        id = id,
        tabId = tabId,
        imageUrl = imageUrl,
        x = x,
        y = y,
        rotation = rotation,
        scale = scale,
        lastModified = lastModified,
        version = version,
        isDeleted = false,
        isPendingSync = isPendingSync
    )
}

fun AssetDto.toEntity(isPendingSync: Boolean = false): AssetEntity {
    return AssetEntity(
        id = id,
        tabId = tabId,
        imageUrl = imageUrl,
        x = x,
        y = y,
        rotation = rotation,
        scale = scale,
        lastModified = lastModified,
        version = version,
        isDeleted = isDeleted,
        isPendingSync = isPendingSync
    )
}

fun AssetEntity.toDto(): AssetDto {
    return AssetDto(
        id = id,
        tabId = tabId,
        imageUrl = imageUrl,
        x = x,
        y = y,
        rotation = rotation,
        scale = scale,
        lastModified = lastModified,
        version = version,
        isDeleted = isDeleted
    )
}
