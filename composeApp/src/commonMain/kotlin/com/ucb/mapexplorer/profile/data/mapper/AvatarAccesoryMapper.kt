package com.ucb.mapexplorer.profile.data.mapper

import com.ucb.mapexplorer.profile.domain.model.AvatarAccessory
import mapexplorer.composeapp.generated.resources.Res
import mapexplorer.composeapp.generated.resources.avatar_acc_booba
import mapexplorer.composeapp.generated.resources.avatar_acc_cake
import mapexplorer.composeapp.generated.resources.avatar_acc_monster
import org.jetbrains.compose.resources.DrawableResource

fun AvatarAccessory.toResource(): DrawableResource? = when (this) {
    AvatarAccessory.NONE    -> null
    AvatarAccessory.BOOBA   -> Res.drawable.avatar_acc_booba
    AvatarAccessory.CAKE    -> Res.drawable.avatar_acc_cake
    AvatarAccessory.MONSTER -> Res.drawable.avatar_acc_monster
}