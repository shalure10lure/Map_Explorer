package com.ucb.mapexplorer.explanation.explanation2.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.ucb.mapexplorer.explanation.explanation2.presentation.state.Explanation2UIState

class Explanation2ViewModel : ViewModel() {
    private val _state = MutableStateFlow(Explanation2UIState())
    val state: StateFlow<Explanation2UIState> = _state.asStateFlow()
}