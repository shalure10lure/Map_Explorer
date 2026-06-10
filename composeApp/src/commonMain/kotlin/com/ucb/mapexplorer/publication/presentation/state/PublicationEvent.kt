package com.ucb.mapexplorer.publication.presentation.state

sealed interface PublicationEvent {
    data class OnRatingSelected(val stars: Int) : PublicationEvent
    data class OnExperienceChanged(val text: String) : PublicationEvent
    data object OnPublishClick : PublicationEvent
    data object OnCancelClick : PublicationEvent
    data object OnDismissError : PublicationEvent
}
