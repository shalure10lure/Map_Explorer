package com.ucb.mapexplorer.di

import com.ucb.mapexplorer.auth.domain.usecase.LoginUseCase
import com.ucb.mapexplorer.auth.domain.usecase.RegisterUseCase
import com.ucb.mapexplorer.dangerzone.domain.usecase.CheckDangerZoneUseCase
import com.ucb.mapexplorer.dangerzone.domain.usecase.SyncDangerZonesUseCase
import com.ucb.mapexplorer.friends.domain.usecase.AcceptFriendRequestUseCase
import com.ucb.mapexplorer.friends.domain.usecase.DeclineFriendRequestUseCase
import com.ucb.mapexplorer.friends.domain.usecase.GetFriendsUseCase
import com.ucb.mapexplorer.friends.domain.usecase.GetPendingRequestsUseCase
import com.ucb.mapexplorer.friends.domain.usecase.GetUserProfileUseCase
import com.ucb.mapexplorer.friends.domain.usecase.HasPendingRequestUseCase
import com.ucb.mapexplorer.friends.domain.usecase.IsFriendUseCase
import com.ucb.mapexplorer.friends.domain.usecase.ObserveFriendRequestsUseCase
import com.ucb.mapexplorer.friends.domain.usecase.RemoveFriendUseCase
import com.ucb.mapexplorer.friends.domain.usecase.SearchUsersUseCase
import com.ucb.mapexplorer.friends.domain.usecase.SendFriendRequestUseCase
import com.ucb.mapexplorer.map.domain.usecase.GetCurrentLocationUseCase
import com.ucb.mapexplorer.map.domain.usecase.GetDiscoveredTilesUseCase
import com.ucb.mapexplorer.map.domain.usecase.SyncMapHistoryUseCase
import com.ucb.mapexplorer.map.domain.usecase.UnlockTileUseCase
import com.ucb.mapexplorer.nearbyplaces.domain.usecase.GetFavoritosUseCase
import com.ucb.mapexplorer.nearbyplaces.domain.usecase.GetGuardadosUseCase
import com.ucb.mapexplorer.nearbyplaces.domain.usecase.GetNearbyPlacesUseCase
import com.ucb.mapexplorer.nearbyplaces.domain.usecase.GetPlaceDetailUseCase
import com.ucb.mapexplorer.nearbyplaces.domain.usecase.IsFavoritoUseCase
import com.ucb.mapexplorer.nearbyplaces.domain.usecase.IsGuardadoUseCase
import com.ucb.mapexplorer.nearbyplaces.domain.usecase.SyncLugarDescubiertoUseCase
import com.ucb.mapexplorer.nearbyplaces.domain.usecase.SyncLugarVisitadoUseCase
import com.ucb.mapexplorer.nearbyplaces.domain.usecase.ToggleFavoritoUseCase
import com.ucb.mapexplorer.nearbyplaces.domain.usecase.ToggleGuardadoUseCase
import com.ucb.mapexplorer.profile.domain.usecase.GetProfileUseCase
import com.ucb.mapexplorer.profile.domain.usecase.ObserveProfileUseCase
import com.ucb.mapexplorer.profile.domain.usecase.UpdateProfileUseCase
import com.ucb.mapexplorer.publication.domain.usecase.GetAllPublicationsUseCase
import com.ucb.mapexplorer.publication.domain.usecase.GetPlaceAverageRatingUseCase
import com.ucb.mapexplorer.publication.domain.usecase.PublishExperienceUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {
    singleOf(::LoginUseCase)
    singleOf(::RegisterUseCase)
    singleOf(::GetDiscoveredTilesUseCase)
    singleOf(::GetCurrentLocationUseCase)
    singleOf(::UnlockTileUseCase)

    // NearbyPlaces
    singleOf(::GetNearbyPlacesUseCase)
    singleOf(::GetPlaceDetailUseCase)
    singleOf(::SyncLugarDescubiertoUseCase)
    singleOf(::SyncLugarVisitadoUseCase)
    singleOf(::SyncMapHistoryUseCase)

    // Favoritos
    singleOf(::GetFavoritosUseCase)
    singleOf(::ToggleFavoritoUseCase)
    singleOf(::IsFavoritoUseCase)

    // Guardados
    singleOf(::GetGuardadosUseCase)
    singleOf(::ToggleGuardadoUseCase)
    singleOf(::IsGuardadoUseCase)

    // Profile
    singleOf(::GetProfileUseCase)
    singleOf(::UpdateProfileUseCase)
    singleOf(::ObserveProfileUseCase)
    //publicacion
    singleOf(::PublishExperienceUseCase)
    singleOf(::GetAllPublicationsUseCase)
    singleOf(::GetPlaceAverageRatingUseCase)

    //friend
    singleOf(::GetFriendsUseCase)
    singleOf(::SearchUsersUseCase)
    singleOf(::SendFriendRequestUseCase)
    singleOf(::GetPendingRequestsUseCase)
    singleOf(::AcceptFriendRequestUseCase)
    singleOf(::DeclineFriendRequestUseCase)
    singleOf(::RemoveFriendUseCase)
    singleOf(::GetUserProfileUseCase)
    singleOf(::IsFriendUseCase)
    singleOf(::HasPendingRequestUseCase)
    singleOf(::ObserveFriendRequestsUseCase)

    //lugar peligroso
    //singleOf(::GetDangerZonesUseCase)
    singleOf(::SyncDangerZonesUseCase)
    singleOf(::CheckDangerZoneUseCase)
}
