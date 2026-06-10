package com.ucb.mapexplorer.publication.presentation.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.ucb.designsystem.components.button.PrimaryButton
import com.ucb.designsystem.components.input.DsTextArea
import com.ucb.designsystem.components.navigation.DsTopAppBar
import com.ucb.designsystem.components.rating.DsRatingBar
import com.ucb.designsystem.theme.AppTheme
import mapexplorer.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import com.ucb.mapexplorer.publication.presentation.state.PublicationEffect
import com.ucb.mapexplorer.publication.presentation.state.PublicationEvent
import com.ucb.mapexplorer.publication.presentation.viewmodel.PublicationViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PublicationScreen(
    placeId: String,
    onBack: () -> Unit,
    onPublished: () -> Unit,
    viewModel: PublicationViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(placeId) {
        viewModel.loadPlace(placeId)
        viewModel.effect.collect { effect ->
            when (effect) {
                PublicationEffect.NavigateBack -> onBack()
                PublicationEffect.PublishedSuccessfully -> onPublished()
                is PublicationEffect.ShowError -> { /* manejado por state */ }
            }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(PublicationEvent.OnDismissError)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = AppTheme.colors.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Header ────────────────────────────────────────────────────
            DsTopAppBar(
                title = stringResource(Res.string.navigationSelector_seeNearbyPlaces),
                onBackClick = onBack,
                backIcon = Icons.AutoMirrored.Filled.ArrowBack
            )

            // ── Imagen del lugar ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(AppTheme.colors.surface),
                contentAlignment = Alignment.Center
            ) {
                val place = state.place
                if (place != null && !place.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalPlatformContext.current)
                            .data(place.imageUrl).build(),
                        contentDescription = place.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = state.place?.categoryIcon ?: "📍",
                        fontSize = 64.sp
                    )
                }
            }

            // ── Nombre del lugar ──────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                Text(
                    text = state.place?.name ?: "Cargando...",
                    style = AppTheme.typography.headlineLarge.copy(fontSize = 20.sp),
                    color = AppTheme.colors.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = state.place?.category ?: "",
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.textSecondary
                    )
                    Text(text = "·", color = AppTheme.colors.textSecondary)
                    Text(text = state.place?.categoryIcon ?: "", fontSize = 14.sp)
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
                color = AppTheme.colors.border.copy(alpha = 0.3f)
            )

            // ── Califica el lugar ─────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
                Text(
                    text = stringResource(Res.string.publishExperience_rating),
                    style = AppTheme.typography.labelLarge,
                    color = Color(0xFF00796B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                DsRatingBar(
                    rating = state.rating,
                    onRatingSelected = { viewModel.onEvent(PublicationEvent.OnRatingSelected(it)) },
                    starIcon = Icons.Default.Star
                )

                if (state.rating > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (state.rating) {
                            1 -> "Muy malo"
                            2 -> "Malo"
                            3 -> "Regular"
                            4 -> "Bueno"
                            5 -> "Excelente"
                            else -> ""
                        },
                        style = AppTheme.typography.bodySmall,
                        color = Color(0xFFFFC107),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
                color = AppTheme.colors.border.copy(alpha = 0.3f)
            )

            // ── Escribe tu opinión ────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
                Text(
                    text = stringResource(Res.string.publishExperience_writeOpinion),
                    style = AppTheme.typography.labelLarge,
                    color = Color(0xFF00796B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                DsTextArea(
                    value = state.experienceText,
                    onValueChange = { viewModel.onEvent(PublicationEvent.OnExperienceChanged(it)) },
                    placeholder = "Cuéntanos tu experiencia en este lugar..."
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${state.experienceText.length}/300",
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.textSecondary,
                    modifier = Modifier.align(Alignment.End)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Experiencia del usuario (label) ───────────────────────────
            Text(
                text = stringResource(Res.string.socialMedia_subtittle_myExperience),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Botones Cancelar / Publicar ───────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PrimaryButton(
                    text = stringResource(Res.string.buttonText_cancel),
                    onClick = { viewModel.onEvent(PublicationEvent.OnCancelClick) },
                    modifier = Modifier.weight(1f).height(52.dp),
                    isPrimary = false
                )

                PrimaryButton(
                    text = "Publicar", // 'Publicar' doesn't have a direct key in strings.xml yet, keeping it or adding one if needed.
                    onClick = { viewModel.onEvent(PublicationEvent.OnPublishClick) },
                    isLoading = state.isPublishing,
                    modifier = Modifier.weight(1f).height(52.dp),
                    isPrimary = true
                )
            }
        }
    }
}