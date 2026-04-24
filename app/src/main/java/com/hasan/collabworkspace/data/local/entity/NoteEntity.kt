package com.hasan.collabworkspace.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String,
    val tabId: String,
    val content: String,
    val orderIndex: Double,
    val createdAt: Long, // Added for default ordering
    val lastModified: Long,
    val version: Int,
    val isDeleted: Boolean,
    val isPendingSync: Boolean = false
)
