package com.hasan.collabworkspace.domain.usecase

import com.hasan.collabworkspace.data.local.entities.NoteEntity
import com.hasan.collabworkspace.domain.repository.WorkspaceRepository

class CreateNoteUseCase(
    private val repository: WorkspaceRepository
) {
    suspend operator fun invoke(content: String, order: Int) {
        val note = NoteEntity(
            content = content,
            order = order,
            lastModified = System.currentTimeMillis(),
            version = 1,
            isDeleted = false
        )
        repository.upsertNote(note)
    }
}