package com.ucb.mapexplorer.map.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ucb.designsystem.components.button.PrimaryButton
import com.ucb.designsystem.theme.AppTheme
import com.ucb.designsystem.theme.ThemeMode
import com.ucb.mapexplorer.core.*
import mapexplorer.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

import com.ucb.mapexplorer.map.presentation.state.MapEffect
import com.ucb.mapexplorer.map.presentation.state.MapEvent
import com.ucb.mapexplorer.map.presentation.viewmodel.MapViewModel
import com.ucb.mapexplorer.navigation.NavRoute
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    viewModel: MapViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Manejar efectos del ViewModel
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MapEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
                is MapEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                is MapEffect.NewTileDiscovered -> {
                    snackbarHostState.showSnackbar("¡Nueva zona descubierta! 🗺️")
                }
                is MapEffect.CenterMapOnLocation -> {
                    // Aquí deberías tener una forma de mover la cámara de OsmDroid
                    // Si usas un state para la cámara o el MapView directamente:
                    // mapView.controller.animateTo(GeoPoint(effect.lat, effect.lon))
                }
                MapEffect.CenterMapOnUser -> { /* Manejado en MapViewContainer */ }
            }
        }
    }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded
        )
    )

    // fillMaxSize sin padding externo — el scaffold vive dentro del Box de MainScreen
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        sheetContent = {
            MapSettingsContent(
                onNavigateToFavoritos = { navController.navigate(NavRoute.FavoritePlaces) },
                onNavigateToGuardados = { navController.navigate(NavRoute.SavedPlaces) }
            )
        },
        sheetPeekHeight = 80.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContainerColor = AppTheme.colors.surface,
        sheetDragHandle = {
            BottomSheetDefaults.DragHandle(
                color = AppTheme.colors.textSecondary.copy(alpha = 0.5f)
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AppTheme.colors.background)
        ) {
            // Mapa principal con Fog of War
            MapViewContainer(
                modifier = Modifier.fillMaxSize(),
                state = state,
                navController = navController,
                onLocationChanged = { lat, lon ->
                    viewModel.onEvent(MapEvent.OnLocationUpdated(lat, lon))
                }
            )

            // Indicador de carga de primera ubicación
            if (state.isLoadingLocation) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppTheme.colors.background.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = AppTheme.colors.primary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Obteniendo tu ubicación...",
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colors.textPrimary
                        )
                    }
                }
            }

            // Diálogo de error
            state.errorMessage?.let { error ->
                AlertDialog(
                    onDismissRequest = { viewModel.onEvent(MapEvent.OnDismissError) },
                    confirmButton = {
                        TextButton(onClick = { viewModel.onEvent(MapEvent.OnDismissError) }) {
                            Text("OK")
                        }
                    },
                    title = { Text("Error") },
                    text = { Text(error) }
                )
            }
        }
    }
}


@Composable
private fun MapSettingsContent(
    onNavigateToFavoritos: () -> Unit,
    onNavigateToGuardados: () -> Unit
) {
    val globalLanguage = LocalAppLanguage.current
    val globalTheme = LocalThemeMode.current
    val changeLanguage = LocalLanguageController.current
    val changeTheme = LocalThemeController.current

    var tempLanguage by remember(globalLanguage) { mutableStateOf(globalLanguage) }
    var tempTheme by remember(globalTheme) { mutableStateOf(globalTheme) }
    val hasChanges = tempLanguage != globalLanguage || tempTheme != globalTheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.colors.surface)
            .padding(horizontal = 24.dp)
            .padding(bottom = 40.dp),
        horizontalAlignment = Alignment.Start
    ) {
        SectionHeader(stringResource(Res.string.moreOptions_tittle_configuration))
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.moreOptions_subtittle_language),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.textPrimary,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingChip(
                text = stringResource(Res.string.moreOptions_optionText_spanish),
                isSelected = tempLanguage == AppLanguage.SPANISH,
                onClick = { tempLanguage = AppLanguage.SPANISH }
            )
            SettingChip(
                text = stringResource(Res.string.moreOptions_optionText_english),
                isSelected = tempLanguage == AppLanguage.ENGLISH,
                onClick = { tempLanguage = AppLanguage.ENGLISH }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(Res.string.moreOptions_subtittle_theme),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.textPrimary,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingChip(
                text = stringResource(Res.string.moreOptions_optionText_bright),
                isSelected = tempTheme == ThemeMode.LIGHT,
                onClick = { tempTheme = ThemeMode.LIGHT }
            )
            SettingChip(
                text = stringResource(Res.string.moreOptions_optionText_dark),
                isSelected = tempTheme == ThemeMode.DARK,
                onClick = { tempTheme = ThemeMode.DARK }
            )
        }

        if (hasChanges) {
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        tempLanguage = globalLanguage
                        tempTheme = globalTheme
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, AppTheme.colors.border),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AppTheme.colors.textPrimary
                    )
                ) {
                    Text(stringResource(Res.string.buttonText_cancel), style = AppTheme.typography.labelLarge)
                }
                PrimaryButton(
                    text = stringResource(Res.string.buttonText_save),
                    onClick = {
                        if (tempLanguage != globalLanguage) changeLanguage(tempLanguage)
                        if (tempTheme != globalTheme) changeTheme(tempTheme)
                    },
                    modifier = Modifier.weight(1f).height(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --- SECCIÓN: MIS GUARDADOS ---
        SectionHeader(stringResource(Res.string.moreOptions_tittle_mySaves))

        Spacer(modifier = Modifier.height(8.dp))

        // Botón Ver favoritos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(AppTheme.colors.background)
                .clickable { onNavigateToFavoritos() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = AppTheme.colors.primary,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = stringResource(Res.string.moreOptions_textSelector_favoritePlaces),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = AppTheme.colors.textSecondary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón Ver guardados
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(AppTheme.colors.background)
                .clickable { onNavigateToGuardados() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Bookmark,
                contentDescription = null,
                tint = AppTheme.colors.primary,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = stringResource(Res.string.moreOptions_textSelector_savedPlaces),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = AppTheme.colors.textSecondary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SectionHeader(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title.uppercase(),
            style = AppTheme.typography.labelLarge,
            color = AppTheme.colors.textSecondary,
            letterSpacing = 1.2.sp
        )
        Spacer(modifier = Modifier.width(12.dp))
        com.ucb.designsystem.components.divider.HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = AppTheme.colors.border.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun SettingChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) AppTheme.colors.primary else AppTheme.colors.textPrimary.copy(alpha = 0.05f),
        contentColor = if (isSelected) Color.White else AppTheme.colors.textPrimary,
        border = if (!isSelected) BorderStroke(1.dp, AppTheme.colors.border.copy(alpha = 0.3f)) else null
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            style = AppTheme.typography.bodyMedium
        )
    }
}
