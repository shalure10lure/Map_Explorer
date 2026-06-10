package com.ucb.mapexplorer.profile.data.mapper

import com.ucb.mapexplorer.profile.domain.model.AvatarHat
import mapexplorer.composeapp.generated.resources.Res
import mapexplorer.composeapp.generated.resources.avatar_som_banan
import mapexplorer.composeapp.generated.resources.avatar_som_hoja
import mapexplorer.composeapp.generated.resources.avatar_som_ice_c
import org.jetbrains.compose.resources.DrawableResource

fun AvatarHat.toResource(): DrawableResource? = when (this) {
    AvatarHat.NONE      -> null
    AvatarHat.BANANA    -> Res.drawable.avatar_som_banan
    AvatarHat.HOJA      -> Res.drawable.avatar_som_hoja
    AvatarHat.ICE_CREAM -> Res.drawable.avatar_som_ice_c
}