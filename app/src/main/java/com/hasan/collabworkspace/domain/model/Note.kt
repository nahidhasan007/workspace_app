package com.hasan.collabworkspace.domain.model

data class Note(
    val id: String,
    val tabId: String,
    val content: String,
    val orderIndex: Double,
    val createdAt: Long,
    val lastModified: Long,
    val version: Int
)
