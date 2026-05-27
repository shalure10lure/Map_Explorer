package com.ucb.mapexplorer.social.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ucb.designsystem.theme.AppTheme
import mapexplorer.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

// Modelo temporal de post social (renombrado para evitar conflictos)
data class SocialPostLegacy(
    val userName: String,
    val placeName: String,
    val placeType: String,
    val rating: Float,
    val experience: String,
    val isOwnPost: Boolean = false
)

@Composable
fun SocialScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }

    // Datos de ejemplo (luego vendrán de Firebase)
    val posts = remember {
        listOf(
            SocialPostLegacy(
                userName = "Personaje Unknown User",
                placeName = "Punto de Encuentro",
                placeType = "Restaurante",
                rating = 4f,
                experience = "Experiencia del usuario sobre el sitio que visitó",
                isOwnPost = true
            ),
            SocialPostLegacy(
                userName = "Amigo1",
                placeName = "Punto de Encuentro",
                placeType = "Restaurante",
                rating = 4f,
                experience = "Experiencia del usuario sobre el sitio que visitó"
            ),
            SocialPostLegacy(
                userName = "Amigo2",
                placeName = "Estadio Félix Capriles",
                placeType = "Estadio",
                rating = 5f,
                experience = "Increíble partido, gran ambiente"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        // ── HEADER con "Volver al Mapa" y ícono de correo ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "← ${stringResource(Res.string.navigationSelector_backToMap)}",
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.textPrimary
            )
            // Ícono de mensajes/solicitudes
            IconButton(onClick = { /* abrir solicitudes de amistad */ }) {
                Text("✉", fontSize = 22.sp)
            }
        }

        // ── BUSCADOR DE PERSONAS ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(AppTheme.colors.surface)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = AppTheme.typography.bodyMedium.copy(
                    color = AppTheme.colors.textPrimary
                ),
                decorationBox = { innerTextField ->
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = stringResource(Res.string.searchText_searchForPerson),
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colors.textSecondary
                        )
                    }
                    innerTextField()
                }
            )
            Text("🔍", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── FEED DE PUBLICACIONES ──
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(posts) { post ->
                SocialPostCard(
                    post = post,
                    onSendFriendRequest = { /* lógica de amistad */ },
                    onViewOnMap = { /* navegar al mapa */ }
                )
            }
        }
    }
}

@Composable
private fun SocialPostCard(
    post: SocialPostLegacy,
    onSendFriendRequest: () -> Unit,
    onViewOnMap: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.colors.background)
    ) {
        // ── Cabecera: Avatar + nombre + acción ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar circular placeholder
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(AppTheme.colors.surface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = post.userName.first().toString(),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = post.userName,
                    style = AppTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.textPrimary
                )
            }

            // Botón "Enviar solicitud de amistad" o "Ver lugar en mapa"
            if (!post.isOwnPost) {
                TextButton(onClick = onSendFriendRequest) {
                    Text(
                        text = stringResource(Res.string.socialMedia_subtittle_sendFriend),
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.primary
                    )
                }
            }
        }

        // ── Imagen del lugar (placeholder) ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AppTheme.colors.surface),
            contentAlignment = Alignment.Center
        ) {
            Text("📍 ${post.placeName}", color = AppTheme.colors.textSecondary)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Info del lugar ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = post.placeName,
                    style = AppTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.textPrimary
                )
                Text(
                    text = post.placeType,
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.textSecondary
                )
            }
            // Estrellas de rating
            Text(
                text = "⭐".repeat(post.rating.toInt()),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // ── Experiencia del usuario ──
        Text(
            text = stringResource(Res.string.socialMedia_subtittle_myExperience),
            style = AppTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.textPrimary
        )
        Text(
            text = post.experience,
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.textPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        // ── "Ver lugar en el mapa" ──
        TextButton(
            onClick = onViewOnMap,
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = stringResource(Res.string.socialMedia_subtittle_viewInMap),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.primary
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            color = AppTheme.colors.border
        )
    }
}
