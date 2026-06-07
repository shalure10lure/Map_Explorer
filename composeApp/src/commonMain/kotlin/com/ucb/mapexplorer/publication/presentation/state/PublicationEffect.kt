package com.ucb.mapexplorer.publication.presentation.state

sealed interface PublicationEffect {
    data object NavigateBack : PublicationEffect
    data object PublishedSuccessfully : PublicationEffect
    data class ShowError(val message: String) : PublicationEffect
}