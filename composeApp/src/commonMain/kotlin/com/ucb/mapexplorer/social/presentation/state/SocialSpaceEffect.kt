package com.ucb.mapexplorer.social.presentation.state

sealed interface SocialSpaceEffect {
    data object NavigateBack : SocialSpaceEffect
    data object NavigateToMessages : SocialSpaceEffect
    data class ShowError(val message: String) : SocialSpaceEffect
    data class ShowToast(val message: String) : SocialSpaceEffect
    data class NavigateToPlaceDetail(val lugarId: String) : SocialSpaceEffect
}