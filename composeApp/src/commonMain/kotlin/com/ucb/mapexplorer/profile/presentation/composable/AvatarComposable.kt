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
import com.ucb.mapexplorer.profile.data.mapper.toResource
import com.ucb.mapexplorer.profile.domain.model.AvatarAccessory
import com.ucb.mapexplorer.profile.domain.model.AvatarBody
import com.ucb.mapexplorer.profile.domain.model.AvatarConfigModel
import com.ucb.mapexplorer.profile.domain.model.AvatarHat
import mapexplorer.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

// Mapea el enum al recurso drawable






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