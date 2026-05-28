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
import androidx.compose.material.icons.filled.Search
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
import com.ucb.mapexplorer.social.presentation.state.SocialPost
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
        // 1. TOP NAVIGATION BAR (ROJA)
        SocialTopBar(
            onNearbyClick = onNavigateToNearby,
            onProfileClick = onNavigateToProfile
        )

        // 2. SEARCH & BACK SECTION
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

            // Search Bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onEvent(SocialSpaceEvent.OnSearchQueryChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(Res.string.searchText_searchForPerson), color = AppTheme.colors.textSecondary) },
                trailingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AppTheme.colors.textSecondary) },
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = AppTheme.colors.surface,
                    focusedContainerColor = AppTheme.colors.surface,
                    focusedTextColor = AppTheme.colors.textPrimary,
                    unfocusedTextColor = AppTheme.colors.textPrimary,
                    cursorColor = AppTheme.colors.primary,
                    focusedBorderColor = AppTheme.colors.primary,
                    unfocusedBorderColor = AppTheme.colors.border
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. FEED LIST
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
fun SocialTopBar(
    onNearbyClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Surface(
        color = Color(0xFFD32F2F), 
        modifier = Modifier.fillMaxWidth().height(100.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationItem(
                label = stringResource(Res.string.navigationBar_socialMedia_textSelection),
                isSelected = true,
                onClick = {}
            )
            NavigationItem(
                label = stringResource(Res.string.navigationBar_nearbyPlaces_textSelection),
                isSelected = false,
                onClick = onNearbyClick
            )
            NavigationItem(
                label = stringResource(Res.string.navigationBar_profile_textSelection),
                isSelected = false,
                onClick = onProfileClick
            )
        }
    }
}

@Composable
fun NavigationItem(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp).clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    if (isSelected) Color.White.copy(alpha = 0.25f) else Color.Transparent,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star, 
                contentDescription = null, 
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(text = label, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SocialPostItem(
    post: SocialPost,
    onAddFriend: (String) -> Unit,
    onViewMap: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // User Info Row
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

        // Image Card with Coil
        Card(
            modifier = Modifier.fillMaxWidth().height(220.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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

        // Details Row
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
                // Rating Stars
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
        
        // Experience Section
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
