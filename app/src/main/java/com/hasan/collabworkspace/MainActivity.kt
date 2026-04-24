package com.hasan.collabworkspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.hasan.collabworkspace.presentation.workspace.WorkspaceScreen
import com.hasan.collabworkspace.presentation.workspace.WorkspaceViewModel
import com.hasan.collabworkspace.ui.theme.CollabworkspaceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as CollabWorkspaceApp
        
        val viewModel: WorkspaceViewModel by viewModels {
            WorkspaceViewModelFactory(app, this)
        }

        setContent {
            CollabworkspaceTheme {
                WorkspaceScreen(viewModel = viewModel)
            }
        }
    }
}

class WorkspaceViewModelFactory(
    private val app: CollabWorkspaceApp,
    owner: SavedStateRegistryOwner
) : AbstractSavedStateViewModelFactory(owner, null) {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(WorkspaceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkspaceViewModel(
                useCases = app.useCases,
                repository = app.repository,
                savedStateHandle = handle
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}