package com.hasan.collabworkspace.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val content: String,
    val order: Int,
    val lastModified: Long,
    val version: Int,
    val isDeleted: Boolean = false
)