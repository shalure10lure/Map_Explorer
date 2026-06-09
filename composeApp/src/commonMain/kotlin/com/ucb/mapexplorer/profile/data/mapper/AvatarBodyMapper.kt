package com.ucb.mapexplorer.profile.data.mapper

import com.ucb.mapexplorer.profile.domain.model.AvatarBody
import mapexplorer.composeapp.generated.resources.Res
import mapexplorer.composeapp.generated.resources.avatar_cu_gall
import mapexplorer.composeapp.generated.resources.avatar_cu_gato
import mapexplorer.composeapp.generated.resources.avatar_cu_pato
import org.jetbrains.compose.resources.DrawableResource

fun AvatarBody.toResource(): DrawableResource = when (this) {
    AvatarBody.GATO    -> Res.drawable.avatar_cu_gato
    AvatarBody.GALLINA -> Res.drawable.avatar_cu_gall
    AvatarBody.PATO    -> Res.drawable.avatar_cu_pato
}