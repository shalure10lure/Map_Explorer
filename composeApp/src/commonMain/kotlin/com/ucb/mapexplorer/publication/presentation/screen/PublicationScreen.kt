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
import com.ucb.designsystem.theme.AppTheme
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = AppTheme.colors.textPrimary,
                    modifier = Modifier.size(22.dp).clickable { onBack() }
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Ver lugares cercanos a mi",
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textPrimary,
                    modifier = Modifier.clickable { onBack() }
                )
            }

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
                    text = "Califica el lugar",
                    style = AppTheme.typography.labelLarge,
                    color = Color(0xFF00796B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..5).forEach { star ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "$star estrellas",
                            tint = if (star <= state.rating) Color(0xFFFFC107) else Color(0xFFE0E0E0),
                            modifier = Modifier
                                .size(44.dp)
                                .clickable {
                                    viewModel.onEvent(PublicationEvent.OnRatingSelected(star))
                                }
                        )
                    }
                }

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
                    text = "Escribe tu opinión",
                    style = AppTheme.typography.labelLarge,
                    color = Color(0xFF00796B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = state.experienceText,
                    onValueChange = {
                        viewModel.onEvent(PublicationEvent.OnExperienceChanged(it))
                    },
                    placeholder = {
                        Text(
                            "Cuéntanos tu experiencia en este lugar...",
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colors.textSecondary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00796B),
                        unfocusedBorderColor = AppTheme.colors.border,
                        focusedContainerColor = AppTheme.colors.surface,
                        unfocusedContainerColor = AppTheme.colors.surface
                    ),
                    textStyle = AppTheme.typography.bodyMedium.copy(
                        color = AppTheme.colors.textPrimary
                    ),
                    maxLines = 6
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
                text = "Experiencia del usuario",
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
                OutlinedButton(
                    onClick = { viewModel.onEvent(PublicationEvent.OnCancelClick) },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AppTheme.colors.textPrimary
                    )
                ) {
                    Text("Cancelar", style = AppTheme.typography.labelLarge)
                }

                Button(
                    onClick = { viewModel.onEvent(PublicationEvent.OnPublishClick) },
                    enabled = !state.isPublishing,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppTheme.colors.primary
                    )
                ) {
                    if (state.isPublishing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Publicar",
                            style = AppTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}