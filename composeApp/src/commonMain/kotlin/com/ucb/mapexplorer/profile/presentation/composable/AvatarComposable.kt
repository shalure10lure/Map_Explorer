package com.ucb.mapexplorer.profile.presentation.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ucb.mapexplorer.profile.domain.model.AvatarAccessory
import com.ucb.mapexplorer.profile.domain.model.AvatarBody
import com.ucb.mapexplorer.profile.domain.model.AvatarConfigModel
import com.ucb.mapexplorer.profile.domain.model.AvatarHat
import mapexplorer.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

// Mapea el enum al recurso drawable
fun AvatarBody.toResource(): DrawableResource = when (this) {
    AvatarBody.GATO    -> Res.drawable.avatar_cu_gato
    AvatarBody.GALLINA -> Res.drawable.avatar_cu_gall
    AvatarBody.PATO    -> Res.drawable.avatar_cu_pato
}

fun AvatarHat.toResource(): DrawableResource? = when (this) {
    AvatarHat.NONE      -> null
    AvatarHat.BANANA    -> Res.drawable.avatar_som_banan
    AvatarHat.HOJA      -> Res.drawable.avatar_som_hoja
    AvatarHat.ICE_CREAM -> Res.drawable.avatar_som_ice_c
}

fun AvatarAccessory.toResource(): DrawableResource? = when (this) {
    AvatarAccessory.NONE    -> null
    AvatarAccessory.BOOBA   -> Res.drawable.avatar_acc_booba
    AvatarAccessory.CAKE    -> Res.drawable.avatar_acc_cake
    AvatarAccessory.MONSTER -> Res.drawable.avatar_acc_monster
}

// Composable principal — superpone las 3 capas
@Composable
fun AvatarDisplay(
    config: AvatarConfigModel,
    size: Dp = 120.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Capa 1 — Cuerpo (siempre presente)
        Image(
            painter = painterResource(config.body.toResource()),
            contentDescription = config.body.displayName,
            modifier = Modifier.size(size),
            contentScale = ContentScale.Fit
        )

        // Capa 2 — Sombrero (opcional)
        config.hat.toResource()?.let { hatRes ->
            Image(
                painter = painterResource(hatRes),
                contentDescription = config.hat.displayName,
                modifier = Modifier.size(size),
                contentScale = ContentScale.Fit
            )
        }

        // Capa 3 — Accesorio (opcional)
        config.accessory.toResource()?.let { accRes ->
            Image(
                painter = painterResource(accRes),
                contentDescription = config.accessory.displayName,
                modifier = Modifier.size(size),
                contentScale = ContentScale.Fit
            )
        }
    }
}