package com.hasan.collabworkspace.domain.usecase

import com.hasan.collabworkspace.data.local.entities.NoteEntity
import com.hasan.collabworkspace.domain.repository.WorkspaceRepository

class UpdateNoteUseCase(
    private val repository: WorkspaceRepository
) {
    suspend operator fun invoke(note: NoteEntity, newContent: String) {
        val updated = note.copy(
            content = newContent,
            lastModified = System.currentTimeMillis(),
            version = note.version + 1
        )
        repository.upsertNote(updated)
    }
}