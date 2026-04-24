package com.hasan.collabworkspace.data.remote.dto

data class NoteDto(
    val id: String = "",
    val tabId: String = "",
    val content: String = "",
    val orderIndex: Double = 0.0,
    val lastModified: Long = 0L,
    val version: Int = 0,
    val isDeleted: Boolean = false
)
