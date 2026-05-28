package com.ucb.mapexplorer.social.presentation.screen

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.social.presentation.state.*
import com.ucb.mapexplorer.social.presentation.viewmodel.SocialSpaceViewModel
import mapexplorer.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SocialSpaceScreen(
    onBack: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToNearby: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: SocialSpaceViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SocialSpaceEffect.NavigateBack -> onBack()
                SocialSpaceEffect.NavigateToMessages -> onNavigateToMessages()
                is SocialSpaceEffect.ShowError -> { /* Mostrar error */ }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        // La TopBar global ya está presente en MainScreen.
        // Aquí solo manejamos el contenido del feed.
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { viewModel.onEvent(SocialSpaceEvent.OnBackClick) }) {
                    Text(
                        text = "← ${stringResource(Res.string.navigationSelector_backToMap)}",
                        color = AppTheme.colors.textPrimary,
                        style = AppTheme.typography.bodyMedium
                    )
                }
                
                IconButton(onClick = { viewModel.onEvent(SocialSpaceEvent.OnMessageClick) }) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Messages",
                        tint = AppTheme.colors.textPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // FEED LIST
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(32.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(state.posts) { post ->
                    SocialPostItem(
                        post = post,
                        onAddFriend = { viewModel.onEvent(SocialSpaceEvent.OnAddFriendClick(it)) },
                        onViewMap = { viewModel.onEvent(SocialSpaceEvent.OnViewOnMapClick(it)) }
                    )
                }
            }
        }
    }
}

@Composable
fun SocialPostItem(
    post: SocialPost,
    onAddFriend: (String) -> Unit,
    onViewMap: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).background(AppTheme.colors.surface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = post.userName.take(1), 
                    style = AppTheme.typography.bodySmall, 
                    color = AppTheme.colors.textSecondary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = post.userName,
                style = AppTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.textPrimary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth().height(220.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface)
        ) {
            if (!post.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = post.locationName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize().background(AppTheme.colors.surface), 
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(Res.drawable.logo_map_explorer),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        alpha = 0.2f
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = post.locationName,
                    style = AppTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.textPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (index < post.rating) Color(0xFFFFC107) else Color.Gray.copy(alpha = 0.3f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = post.category,
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.textSecondary
                    )
                }
            }

            Text(
                text = if (post.isFriend) stringResource(Res.string.socialMedia_subtittle_viewInMap) 
                       else stringResource(Res.string.socialMedia_subtittle_sendFriend),
                color = Color(0xFF2196F3),
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                modifier = Modifier.clickable { 
                    if (post.isFriend) onViewMap(post.id) else onAddFriend(post.id)
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = stringResource(Res.string.socialMedia_subtittle_myExperience),
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.textSecondary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = post.userExperience,
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.textPrimary
        )
    }
}
