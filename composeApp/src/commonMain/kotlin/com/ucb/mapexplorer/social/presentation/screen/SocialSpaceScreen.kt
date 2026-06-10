package com.ucb.mapexplorer.social.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
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
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.profile.domain.model.AvatarConfigModel
import com.ucb.mapexplorer.profile.presentation.composable.AvatarDisplay
import com.ucb.mapexplorer.social.presentation.composable.SocialPostItem
import com.ucb.mapexplorer.social.presentation.state.*
import com.ucb.mapexplorer.social.presentation.viewmodel.SocialSpaceViewModel
import mapexplorer.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SocialSpaceScreen(
    onBack: () -> Unit,
    onNavigateToFriendsRequests: () -> Unit,
    onNavigateToNearby: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToPlaceDetail: (String) -> Unit,
    viewModel: SocialSpaceViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SocialSpaceEffect.NavigateBack       -> onBack()
                SocialSpaceEffect.NavigateToMessages -> onNavigateToFriendsRequests()
                is SocialSpaceEffect.ShowError       -> snackbarHostState.showSnackbar(effect.message)
                is SocialSpaceEffect.ShowToast       -> snackbarHostState.showSnackbar(effect.message)
                is SocialSpaceEffect.NavigateToPlaceDetail ->  onNavigateToPlaceDetail(effect.lugarId)
            }
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
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { viewModel.onEvent(SocialSpaceEvent.OnBackClick) }) {
                    Text(
                        "← ${stringResource(Res.string.navigationSelector_backToMap)}",
                        color = AppTheme.colors.textPrimary,
                        style = AppTheme.typography.bodyMedium
                    )
                }
                IconButton(onClick = { viewModel.onEvent(SocialSpaceEvent.OnMessageClick) }) {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = "Solicitudes de amistad",
                        tint = AppTheme.colors.textPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppTheme.colors.primary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    items(state.posts, key = { it.id }) { post ->
                        SocialPostItem(
                            post      = post,
                            onAddFriend = { viewModel.onEvent(SocialSpaceEvent.OnAddFriendClick(post.authorUid)) },
                            onViewPlaceDetail = { lugarId ->   // ← RENOMBRADO
                                viewModel.onEvent(SocialSpaceEvent.OnViewPlaceDetail(lugarId))
                            }
                        )
                    }
                }
            }
        }
    }
}
