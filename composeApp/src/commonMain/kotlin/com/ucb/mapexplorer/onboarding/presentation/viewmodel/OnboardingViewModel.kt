package com.ucb.mapexplorer.onboarding.presentation.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.onboarding.domain.model.OnboardingPageModel
import com.ucb.mapexplorer.onboarding.domain.repository.OnboardingRepository
import com.ucb.mapexplorer.onboarding.utils.getDeviceLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingUIState(
    val pages: List<OnboardingPageModel> = emptyList(),
    val currentIndex: Int = 0,
    val isLoading: Boolean = true,
    val navigateToHome: Boolean = false
)

sealed class OnboardingEvent {
    object Next  : OnboardingEvent()
    object Back  : OnboardingEvent()
    object Skip  : OnboardingEvent()  // No persiste → reaparece al reiniciar
    object Start : OnboardingEvent()  // Persiste → nunca más aparece
}

class OnboardingViewModel(
    private val repository: OnboardingRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUIState())
    val state: StateFlow<OnboardingUIState> = _state

    init { loadPages() }

    private fun loadPages() {
        viewModelScope.launch {
            val lang = getDeviceLanguage()
            val pages = repository.getPages(lang)
            _state.update { it.copy(pages = pages, isLoading = false) }
        }
    }

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            OnboardingEvent.Next  -> _state.update {
                it.copy(currentIndex = (it.currentIndex + 1).coerceAtMost(it.pages.lastIndex))
            }
            OnboardingEvent.Back  -> _state.update {
                it.copy(currentIndex = (it.currentIndex - 1).coerceAtLeast(0))
            }
            OnboardingEvent.Skip  -> _state.update { it.copy(navigateToHome = true) }
            OnboardingEvent.Start -> viewModelScope.launch {
                repository.markOnboardingCompleted()
                _state.update { it.copy(navigateToHome = true) }
            }
        }
    }
}