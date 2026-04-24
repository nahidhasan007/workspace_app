package com.hasan.collabworkspace.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String,
    val tabId: String, // To support Multi-Note Tabs (each tab is a canvas/document)
    val content: String,
    val orderIndex: Double, // 'order' is a SQL keyword, using orderIndex
    val lastModified: Long,
    val version: Int,
    val isDeleted: Boolean,
    val isPendingSync: Boolean = false
)
