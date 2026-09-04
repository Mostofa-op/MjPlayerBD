package com.example.ui.library

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.VideoHistoryEntity
import com.example.data.model.VideoItem
import com.example.data.repository.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class LibraryTab(val title: String) {
    FEATURED_4K_8K("4K & 8K Ultra HD"),
    DEVICE_VIDEOS("Device Storage"),
    HISTORY("Recent History"),
    FAVORITES("Favorites")
}

data class LibraryUiState(
    val selectedTab: LibraryTab = LibraryTab.FEATURED_4K_8K,
    val deviceVideos: List<VideoItem> = emptyList(),
    val isScanning: Boolean = false,
    val errorMessage: String? = null
)

class LibraryViewModel(private val repository: VideoRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    val historyList: StateFlow<List<VideoHistoryEntity>> = repository.getPlaybackHistoryFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val favoritesList: StateFlow<List<VideoHistoryEntity>> = repository.getFavoritesFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val demoVideos: List<VideoItem> = repository.demoVideos

    init {
        scanDeviceVideos()
    }

    fun selectTab(tab: LibraryTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun scanDeviceVideos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isScanning = true)
            val videos = repository.queryDeviceVideos()
            _uiState.value = _uiState.value.copy(
                deviceVideos = videos,
                isScanning = false
            )
        }
    }

    fun handlePickedVideoUri(uri: Uri, onReady: (VideoItem) -> Unit) {
        viewModelScope.launch {
            val item = repository.createVideoFromUri(uri)
            onReady(item)
        }
    }

    fun handleStreamUrl(url: String, title: String, onReady: (VideoItem) -> Unit) {
        val cleanUrl = url.trim()
        if (cleanUrl.isNotEmpty()) {
            val item = VideoItem(
                id = "stream_${System.currentTimeMillis()}",
                title = title.ifBlank { "Ultra HD Stream" },
                uriString = cleanUrl,
                durationMs = 0L,
                resolutionLabel = "4K / 8K Live Stream",
                width = 3840,
                height = 2160,
                fps = 60,
                codec = "HEVC / HLS / DASH",
                hdrType = "Dynamic HDR",
                audioChannels = "Spatial Surround",
                isDemo = false,
                aiSceneTag = "Live High-Bitrate Broadcast",
                aiRecommendation = "AI Super Resolution Active"
            )
            onReady(item)
        }
    }

    fun toggleFavorite(video: VideoItem) {
        viewModelScope.launch {
            repository.toggleFavorite(video.uriString, video.isFavorite)
        }
    }

    fun saveProgress(video: VideoItem, positionMs: Long) {
        viewModelScope.launch {
            repository.saveProgress(video, positionMs)
        }
    }
}
