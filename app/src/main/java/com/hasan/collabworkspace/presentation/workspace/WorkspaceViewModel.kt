package com.hasan.collabworkspace.presentation.workspace

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasan.collabworkspace.domain.repository.WorkspaceRepository
import com.hasan.collabworkspace.domain.usecase.WorkspaceUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WorkspaceViewModel(
    private val useCases: WorkspaceUseCases,
    private val repository: WorkspaceRepository, // Needed for conflict observation, or can add usecases for them
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(WorkspaceState())
    val state: StateFlow<WorkspaceState> = _state.asStateFlow()

    private var observeJob: Job? = null
    private var syncJob: Job? = null

    init {
        // Restore active tab from SavedStateHandle on process death recovery
        val savedTabId = savedStateHandle.get<String>("activeTabId") ?: "default_tab"
        handleIntent(WorkspaceIntent.SwitchTab(savedTabId))

        observeConflicts()
    }

    fun handleIntent(intent: WorkspaceIntent) {
        when (intent) {
            is WorkspaceIntent.SwitchTab -> {
                savedStateHandle["activeTabId"] = intent.tabId
                _state.update { it.copy(activeTabId = intent.tabId) }
                startObservingTab(intent.tabId)
            }
            is WorkspaceIntent.AddNote -> viewModelScope.launch { useCases.saveNote(intent.note) }
            is WorkspaceIntent.UpdateNoteContent -> updateNoteContent(intent.noteId, intent.content)
            is WorkspaceIntent.MoveNote -> moveNote(intent.noteId, intent.newOrderIndex)
            is WorkspaceIntent.DeleteNote -> viewModelScope.launch { useCases.deleteNote(intent.noteId) }
            
            is WorkspaceIntent.AddAsset -> viewModelScope.launch { useCases.saveAsset(intent.asset) }
            is WorkspaceIntent.TransformAsset -> transformAsset(intent.assetId, intent.x, intent.y, intent.rotation, intent.scale)
            is WorkspaceIntent.DeleteAsset -> viewModelScope.launch { useCases.deleteAsset(intent.assetId) }

            is WorkspaceIntent.ToggleDebugHud -> _state.update { it.copy(showDebugHud = !it.showDebugHud) }
            
            is WorkspaceIntent.ResolveNoteConflict -> resolveNoteConflict(intent)
            is WorkspaceIntent.ResolveAssetConflict -> resolveAssetConflict(intent)
        }
    }

    private fun startObservingTab(tabId: String) {
        observeJob?.cancel()
        syncJob?.cancel()

        observeJob = viewModelScope.launch {
            launch {
                useCases.observeNotes(tabId).collect { notes ->
                    _state.update { it.copy(notes = notes) }
                }
            }
            launch {
                useCases.observeAssetsByTab(tabId).collect { assets ->
                    _state.update { it.copy(assets = assets) }
                }
            }
        }

        syncJob = viewModelScope.launch {
            useCases.syncWorkspace(tabId)
        }
    }

    private fun observeConflicts() {
        viewModelScope.launch {
            launch {
                repository.noteConflicts.collect { conflictPair ->
                    _state.update { it.copy(pendingNoteConflict = conflictPair) }
                }
            }
            launch {
                repository.assetConflicts.collect { conflictPair ->
                    _state.update { it.copy(pendingAssetConflict = conflictPair) }
                }
            }
        }
    }

    private fun updateNoteContent(noteId: String, content: String) {
        val note = _state.value.notes.find { it.id == noteId } ?: return
        viewModelScope.launch { useCases.saveNote(note.copy(content = content)) }
    }

    private fun moveNote(noteId: String, newOrderIndex: Double) {
        val note = _state.value.notes.find { it.id == noteId } ?: return
        viewModelScope.launch { useCases.saveNote(note.copy(orderIndex = newOrderIndex)) }
    }

    private fun transformAsset(assetId: String, x: Float, y: Float, rotation: Float, scale: Float) {
        val asset = _state.value.assets.find { it.id == assetId } ?: return
        viewModelScope.launch { useCases.saveAsset(asset.copy(x = x, y = y, rotation = rotation, scale = scale)) }
    }

    private fun resolveNoteConflict(intent: WorkspaceIntent.ResolveNoteConflict) {
        viewModelScope.launch {
            useCases.resolveConflict.resolveNote(intent.resolvedNote)
            _state.update { it.copy(pendingNoteConflict = null) }
        }
    }

    private fun resolveAssetConflict(intent: WorkspaceIntent.ResolveAssetConflict) {
        viewModelScope.launch {
            useCases.resolveConflict.resolveAsset(intent.resolvedAsset)
            _state.update { it.copy(pendingAssetConflict = null) }
        }
    }
}
