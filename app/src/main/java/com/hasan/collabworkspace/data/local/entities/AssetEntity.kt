package com.hasan.collabworkspace.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "assets")
data class AssetEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val noteId: String,          // Foreign key reference to Note
    val uri: String,             // Image URI
    val posX: Float,
    val posY: Float,
    val rotation: Float,
    val scale: Float,
    val lastModified: Long,
    val version: Int,
    val isDeleted: Boolean = false
)
