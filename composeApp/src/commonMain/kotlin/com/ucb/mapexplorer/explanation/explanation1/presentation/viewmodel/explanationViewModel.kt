package com.ucb.mapexplorer.explanation.explanation1.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.ucb.mapexplorer.explanation.explanation1.presentation.state.Explanation1UIState

class Explanation1ViewModel : ViewModel() {
    private val _state = MutableStateFlow(Explanation1UIState())
    val state: StateFlow<Explanation1UIState> = _state.asStateFlow()
}