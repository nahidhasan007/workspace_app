package com.hasan.collabworkspace.domain.model

data class Asset(
    val id: String,
    val tabId: String,
    val imageUrl: String,
    val x: Float,
    val y: Float,
    val rotation: Float,
    val scale: Float,
    val lastModified: Long,
    val version: Int
)
