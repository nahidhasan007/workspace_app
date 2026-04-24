package com.hasan.collabworkspace.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "assets",
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("noteId")]
)
data class AssetEntity(
    @PrimaryKey val id: String,
    val noteId: String,
    val imageUrl: String,
    val x: Float,
    val y: Float,
    val rotation: Float,
    val scale: Float,
    val lastModified: Long,
    val version: Int,
    val isDeleted: Boolean,
    val isPendingSync: Boolean = false
)
