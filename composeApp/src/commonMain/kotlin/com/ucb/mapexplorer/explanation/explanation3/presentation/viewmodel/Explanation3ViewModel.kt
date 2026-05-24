package com.ucb.mapexplorer.explanation.explanation3.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.ucb.mapexplorer.explanation.explanation3.presentation.state.Explanation3UIState

class Explanation3ViewModel : ViewModel() {
    private val _state = MutableStateFlow(Explanation3UIState())
    val state: StateFlow<Explanation3UIState> = _state.asStateFlow()
}