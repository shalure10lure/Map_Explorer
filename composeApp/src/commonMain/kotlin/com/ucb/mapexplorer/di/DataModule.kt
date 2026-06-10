package com.ucb.mapexplorer.di

import com.ucb.mapexplorer.auth.data.datasource.FirebaseManager
import com.ucb.mapexplorer.auth.data.repository.AuthRepositoryImpl
import com.ucb.mapexplorer.auth.domain.repository.AuthRepository
import com.ucb.mapexplorer.dangerzone.data.datasource.DangerZoneRemoteDataSource
import com.ucb.mapexplorer.dangerzone.data.repository.DangerZoneRepositoryImpl
import com.ucb.mapexplorer.dangerzone.domain.repository.DangerZoneRepository
import com.ucb.mapexplorer.friends.data.datasource.FriendsRemoteDataSource
import com.ucb.mapexplorer.friends.data.repository.FriendsRepositoryImpl
import com.ucb.mapexplorer.friends.domain.repository.FriendsRepository
import com.ucb.mapexplorer.map.data.datasource.MapLocalDataSource
import com.ucb.mapexplorer.map.data.datasource.MapRemoteDataSource
import com.ucb.mapexplorer.map.data.repository.MapRepositoryImpl
import com.ucb.mapexplorer.map.data.service.LocalitationService
import com.ucb.mapexplorer.map.domain.repository.MapRepository
import com.ucb.mapexplorer.nearbyplaces.data.datasource.LugarFavoritoLocalDataSource
import com.ucb.mapexplorer.nearbyplaces.data.datasource.LugarGuardadoLocalDataSource
import com.ucb.mapexplorer.nearbyplaces.data.datasource.NearbyPlacesLocalDataSource
import com.ucb.mapexplorer.nearbyplaces.data.datasource.NearbyPlacesRemoteDataSource
import com.ucb.mapexplorer.nearbyplaces.data.repository.FavoritosRepositoryImpl
import com.ucb.mapexplorer.nearbyplaces.data.repository.GuardadosRepositoryImpl
import com.ucb.mapexplorer.nearbyplaces.data.repository.NearbyPlacesRepositoryImpl
import com.ucb.mapexplorer.nearbyplaces.domain.repository.FavoritosRepository
import com.ucb.mapexplorer.nearbyplaces.domain.repository.GuardadosRepository
import com.ucb.mapexplorer.nearbyplaces.domain.repository.NearbyPlacesRepository
import com.ucb.mapexplorer.profile.data.repository.ProfileRepositoryImpl
import com.ucb.mapexplorer.profile.domain.repository.ProfileRepository
import com.ucb.mapexplorer.publication.data.datasource.PublicationRemoteDataSource
import com.ucb.mapexplorer.publication.data.repository.PublicationRepositoryImpl
import com.ucb.mapexplorer.publication.domain.repository.PublicationRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
    single { FirebaseManager() }
    singleOf(::MapLocalDataSource)
    single { MapRemoteDataSource() }
    single { LocalitationService() }
    singleOf(::MapRepositoryImpl).bind<MapRepository>()

    // Profile
    singleOf(::ProfileRepositoryImpl).bind<ProfileRepository>()
// NearbyPlaces
    single { NearbyPlacesRemoteDataSource() }
    singleOf(::NearbyPlacesLocalDataSource)
    singleOf(::NearbyPlacesRepositoryImpl).bind<NearbyPlacesRepository>()


    single { LugarGuardadoLocalDataSource(get()) }
    single { LugarFavoritoLocalDataSource(get()) }

    singleOf(::FavoritosRepositoryImpl).bind<FavoritosRepository>()
    singleOf(::GuardadosRepositoryImpl).bind<GuardadosRepository>()

    single { PublicationRemoteDataSource() }
    singleOf(::PublicationRepositoryImpl).bind<PublicationRepository>()

    single { FriendsRemoteDataSource() }
   singleOf(::FriendsRepositoryImpl).bind<FriendsRepository>()

    single { DangerZoneRemoteDataSource() }
    singleOf(::DangerZoneRepositoryImpl).bind<DangerZoneRepository>()
}
