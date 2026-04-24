package com.hasan.collabworkspace.data.remote.dto

data class AssetDto(
    val id: String = "",
    val tabId: String = "",
    val imageUrl: String = "",
    val x: Float = 0f,
    val y: Float = 0f,
    val rotation: Float = 0f,
    val scale: Float = 1f,
    val lastModified: Long = 0L,
    val version: Int = 0,
    val isDeleted: Boolean = false
)
