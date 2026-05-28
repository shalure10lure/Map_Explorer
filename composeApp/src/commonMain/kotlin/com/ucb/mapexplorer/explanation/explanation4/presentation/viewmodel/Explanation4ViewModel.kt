package com.ucb.mapexplorer.explanation.explanation4.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.ucb.mapexplorer.explanation.explanation4.presentation.state.Explanation4UIState

class Explanation4ViewModel : ViewModel() {
    private val _state = MutableStateFlow(Explanation4UIState())
    val state: StateFlow<Explanation4UIState> = _state.asStateFlow()
}