package com.example.ui.library

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.VideoItem
import com.example.ui.components.LiquidBadge
import com.example.ui.components.LiquidCard
import com.example.ui.theme.LiquidAmber
import com.example.ui.theme.LiquidCyan
import com.example.ui.theme.LiquidDarkObsidian
import com.example.ui.theme.LiquidDeepVoid
import com.example.ui.theme.LiquidGlassBorder
import com.example.ui.theme.LiquidNeonPurple
import com.example.ui.theme.LiquidTextMuted
import com.example.ui.theme.LiquidTextPrimary
import com.example.ui.theme.LiquidTextSecondary
import com.example.ui.theme.LiquidVividPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,
    onVideoSelected: (VideoItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val historyList by viewModel.historyList.collectAsState()
    val favoritesList by viewModel.favoritesList.collectAsState()

    var showStreamDialog by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.handlePickedVideoUri(it) { item ->
                onVideoSelected(item)
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.scanDeviceVideos()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        LiquidDarkObsidian,
                        LiquidDeepVoid,
                        Color(0xFF04060C)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // HERO HEADER
            item {
                HeroHeader(
                    onPickFile = {
                        filePickerLauncher.launch(arrayOf("video/*"))
                    },
                    onOpenStreamDialog = { showStreamDialog = true },
                    onScanStorage = {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_VIDEO)
                        } else {
                            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    }
                )
            }

            // CATEGORY TABS
            item {
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(LibraryTab.entries) { tab ->
                        val isSelected = uiState.selectedTab == tab
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) {
                                        Brush.linearGradient(
                                            listOf(
                                                LiquidCyan.copy(alpha = 0.25f),
                                                LiquidNeonPurple.copy(alpha = 0.25f)
                                            )
                                        )
                                    } else {
                                        Brush.linearGradient(
                                            listOf(
                                                Color(0x221E293B),
                                                Color(0x111E293B)
                                            )
                                        )
                                    }
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) LiquidCyan else LiquidGlassBorder,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { viewModel.selectTab(tab) }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab.title,
                                color = if (isSelected) LiquidCyan else LiquidTextSecondary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // CONTENT SECTION BASED ON TAB
            when (uiState.selectedTab) {
                LibraryTab.FEATURED_4K_8K -> {
                    items(viewModel.demoVideos) { video ->
                        VideoItemCard(
                            video = video,
                            onClick = { onVideoSelected(video) },
                            onToggleFavorite = { viewModel.toggleFavorite(video) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }

                LibraryTab.DEVICE_VIDEOS -> {
                    if (uiState.isScanning) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = LiquidCyan)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Scanning Device for 4K / 8K Videos...",
                                        color = LiquidTextSecondary,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    } else if (uiState.deviceVideos.isEmpty()) {
                        item {
                            EmptyStateCard(
                                title = "No Local Videos Detected",
                                subtitle = "Grant storage permission or use 'Open File' to play any 4K/8K MKV, MP4, or WEBM directly.",
                                buttonText = "Pick Video File",
                                onClick = { filePickerLauncher.launch(arrayOf("video/*")) }
                            )
                        }
                    } else {
                        items(uiState.deviceVideos) { video ->
                            VideoItemCard(
                                video = video,
                                onClick = { onVideoSelected(video) },
                                onToggleFavorite = { viewModel.toggleFavorite(video) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                LibraryTab.HISTORY -> {
                    if (historyList.isEmpty()) {
                        item {
                            EmptyStateCard(
                                title = "No Playback History",
                                subtitle = "Videos you play will remember exact timestamps, bookmarks, and audio gain settings.",
                                buttonText = "Play 8K Cosmic Demo",
                                onClick = {
                                    viewModel.demoVideos.firstOrNull()?.let { onVideoSelected(it) }
                                }
                            )
                        }
                    } else {
                        items(historyList) { history ->
                            val matchedDemo = viewModel.demoVideos.firstOrNull { it.uriString == history.uriString }
                            val item = matchedDemo?.copy(lastPositionMs = history.lastPositionMs) ?: VideoItem(
                                id = "hist_${history.uriString.hashCode()}",
                                title = history.title,
                                uriString = history.uriString,
                                durationMs = history.durationMs,
                                resolutionLabel = history.resolutionLabel,
                                codec = history.codec,
                                audioChannels = history.audioCodec,
                                lastPositionMs = history.lastPositionMs,
                                isFavorite = history.isFavorite
                            )
                            VideoItemCard(
                                video = item,
                                onClick = { onVideoSelected(item) },
                                onToggleFavorite = { viewModel.toggleFavorite(item) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                LibraryTab.FAVORITES -> {
                    val favDemos = viewModel.demoVideos.filter { it.isFavorite }
                    val allFavs = favDemos + favoritesList.map { fav ->
                        VideoItem(
                            id = "fav_${fav.uriString.hashCode()}",
                            title = fav.title,
                            uriString = fav.uriString,
                            durationMs = fav.durationMs,
                            resolutionLabel = fav.resolutionLabel,
                            codec = fav.codec,
                            audioChannels = fav.audioCodec,
                            isFavorite = true
                        )
                    }

                    if (allFavs.isEmpty()) {
                        item {
                            EmptyStateCard(
                                title = "No Favorites Saved",
                                subtitle = "Tap the heart icon on any video to pin your favorite 4K and 8K master streams here.",
                                buttonText = "Explore 4K Demos",
                                onClick = { viewModel.selectTab(LibraryTab.FEATURED_4K_8K) }
                            )
                        }
                    } else {
                        items(allFavs) { video ->
                            VideoItemCard(
                                video = video,
                                onClick = { onVideoSelected(video) },
                                onToggleFavorite = { viewModel.toggleFavorite(video) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // STREAM URL DIALOG
        if (showStreamDialog) {
            StreamUrlDialog(
                onDismiss = { showStreamDialog = false },
                onPlay = { url, title ->
                    showStreamDialog = false
                    viewModel.handleStreamUrl(url, title) { item ->
                        onVideoSelected(item)
                    }
                }
            )
        }
    }
}

@Composable
fun HeroHeader(
    onPickFile: () -> Unit,
    onOpenStreamDialog: () -> Unit,
    onScanStorage: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Top Branding
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(LiquidCyan, LiquidNeonPurple)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = LiquidDarkObsidian,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "LIQUID LUSTRE",
                        color = LiquidTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(LiquidCyan)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "AI 4K & 8K Ultra Engine",
                            color = LiquidCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            LiquidBadge(
                text = "ZERO-LAG HW+",
                backgroundColor = Color(0x337928CA),
                textColor = LiquidNeonPurple
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Quick Actions Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionButton(
                icon = Icons.Default.FolderOpen,
                label = "Open Local",
                accentColor = LiquidCyan,
                onClick = onPickFile,
                modifier = Modifier.weight(1f)
            )

            QuickActionButton(
                icon = Icons.Default.AddLink,
                label = "Stream URL",
                accentColor = LiquidNeonPurple,
                onClick = onOpenStreamDialog,
                modifier = Modifier.weight(1f)
            )

            QuickActionButton(
                icon = Icons.Default.Refresh,
                label = "Scan Device",
                accentColor = LiquidAmber,
                onClick = onScanStorage,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x3317233D))
            .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = accentColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = LiquidTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun VideoItemCard(
    video: VideoItem,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x33121C30))
            .border(1.dp, LiquidGlassBorder, RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {
        Column {
            // Thumbnail / Poster Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(Color(0xFF0E1626))
            ) {
                if (video.posterResId != null) {
                    Image(
                        painter = painterResource(id = video.posterResId),
                        contentDescription = video.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = null,
                            tint = LiquidCyan.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                // Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0x40000000), Color(0xCC060913))
                            )
                        )
                )

                // Top Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LiquidBadge(
                        text = video.resolutionLabel,
                        backgroundColor = if (video.resolutionLabel.contains("8K")) Color(0xCC8B5CF6) else Color(0xCC00F5D4),
                        textColor = if (video.resolutionLabel.contains("8K")) Color.White else LiquidDarkObsidian
                    )

                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x66000000))
                    ) {
                        Icon(
                            imageVector = if (video.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (video.isFavorite) LiquidVividPink else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Center Glass Play Button
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(LiquidCyan.copy(alpha = 0.9f), LiquidNeonPurple.copy(alpha = 0.9f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = LiquidDarkObsidian,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Bottom Duration Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xCC000000))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = video.formattedDuration(),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Info Details
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = video.title,
                    color = LiquidTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = LiquidCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = video.aiSceneTag,
                        color = LiquidCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${video.fps} FPS • ${video.codec}",
                        color = LiquidTextSecondary,
                        fontSize = 11.sp
                    )
                }

                if (video.lastPositionMs > 0L) {
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { video.progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = LiquidCyan,
                        trackColor = Color(0x33FFFFFF)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    title: String,
    subtitle: String,
    buttonText: String,
    onClick: () -> Unit
) {
    LiquidCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0x2238BDF8)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.VideoLibrary,
                    contentDescription = null,
                    tint = LiquidCyan,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                color = LiquidTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subtitle,
                color = LiquidTextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LiquidCyan,
                    contentColor = LiquidDarkObsidian
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = buttonText,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun StreamUrlDialog(
    onDismiss: () -> Unit,
    onPlay: (url: String, title: String) -> Unit
) {
    var urlText by remember { mutableStateOf("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4") }
    var titleText by remember { mutableStateOf("Tears of Steel 4K UHD Master") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xF2080E1C),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AddLink,
                    contentDescription = null,
                    tint = LiquidCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Play Direct Stream",
                    color = LiquidTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "Supports 4K/8K direct streams, HLS (.m3u8), DASH, MP4, MKV with zero buffering lag.",
                    color = LiquidTextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text("Stream Title") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LiquidCyan,
                        unfocusedBorderColor = LiquidGlassBorder,
                        focusedTextColor = LiquidTextPrimary,
                        unfocusedTextColor = LiquidTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = urlText,
                    onValueChange = { urlText = it },
                    label = { Text("Network URL (HLS / MP4 / DASH)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LiquidCyan,
                        unfocusedBorderColor = LiquidGlassBorder,
                        focusedTextColor = LiquidTextPrimary,
                        unfocusedTextColor = LiquidTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onPlay(urlText, titleText) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = LiquidCyan,
                    contentColor = LiquidDarkObsidian
                )
            ) {
                Text("Play Stream", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = LiquidTextSecondary)
            }
        }
    )
}
