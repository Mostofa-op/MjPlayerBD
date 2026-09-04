package com.example.data.repository

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.R
import com.example.data.local.LiquidPlayerDatabase
import com.example.data.local.VideoHistoryEntity
import com.example.data.model.VideoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class VideoRepository(private val context: Context) {
    private val database = LiquidPlayerDatabase.getDatabase(context)
    private val historyDao = database.videoHistoryDao()

    val demoVideos = listOf(
        VideoItem(
            id = "demo_8k_cosmic",
            title = "Cosmic Nebula Odyssey (8K HDR Demo)",
            uriString = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
            durationMs = 734000L,
            resolutionLabel = "8K UHD",
            width = 7680,
            height = 4320,
            fps = 60,
            codec = "AV1 / HEVC",
            hdrType = "HDR10+ Dynamic",
            audioChannels = "Dolby Atmos 7.1",
            sizeBytes = 4294967296L,
            posterResId = R.drawable.poster_cosmic,
            isDemo = true,
            aiSceneTag = "Space & Cosmic Dynamics",
            aiRecommendation = "AI HDR Vivid + HW+ Super-Boost Active"
        ),
        VideoItem(
            id = "demo_4k_cyberpunk",
            title = "Neo Cyberpunk 2099 Nightlife (4K 60FPS)",
            uriString = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            durationMs = 596000L,
            resolutionLabel = "4K UHD",
            width = 3840,
            height = 2160,
            fps = 60,
            codec = "HEVC / H.265",
            hdrType = "Dolby Vision",
            audioChannels = "DTS-HD Master 5.1",
            sizeBytes = 2852126720L,
            posterResId = R.drawable.poster_cyberpunk,
            isDemo = true,
            aiSceneTag = "High Neon Contrast Night Scene",
            aiRecommendation = "AI Crystal Clarity + Super Motion Smoother"
        ),
        VideoItem(
            id = "demo_4k_nature",
            title = "Amazon Emerald Wonders (4K Ultra HD)",
            uriString = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            durationMs = 653000L,
            resolutionLabel = "4K UHD",
            width = 3840,
            height = 2160,
            fps = 60,
            codec = "VP9 / HEVC",
            hdrType = "HLG HDR",
            audioChannels = "Dolby Digital Plus 5.1",
            sizeBytes = 1952126720L,
            posterResId = R.drawable.poster_nature,
            isDemo = true,
            aiSceneTag = "Vibrant Wildlife & Flora",
            aiRecommendation = "AI HDR Vivid Color Tuned"
        ),
        VideoItem(
            id = "demo_4k_sintel",
            title = "Dragon Kin: Sintel Chronicles (4K Cinema)",
            uriString = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
            durationMs = 888000L,
            resolutionLabel = "4K Cinema",
            width = 4096,
            height = 1744,
            fps = 60,
            codec = "H.264 / AVC High Profile",
            hdrType = "DCI-P3 Wide Gamut",
            audioChannels = "Stereo Hi-Res 96kHz",
            sizeBytes = 1420126720L,
            posterResId = R.drawable.poster_cosmic,
            isDemo = true,
            aiSceneTag = "Cinematic Action & Dialogue",
            aiRecommendation = "AI Vocal Boost & Night Vision"
        )
    )

    fun getPlaybackHistoryFlow(): Flow<List<VideoHistoryEntity>> {
        return historyDao.getAllHistory().flowOn(Dispatchers.IO)
    }

    fun getFavoritesFlow(): Flow<List<VideoHistoryEntity>> {
        return historyDao.getFavorites().flowOn(Dispatchers.IO)
    }

    suspend fun getSavedPosition(uriString: String): Long {
        return withContext(Dispatchers.IO) {
            historyDao.getByUri(uriString)?.lastPositionMs ?: 0L
        }
    }

    suspend fun saveProgress(video: VideoItem, positionMs: Long) {
        withContext(Dispatchers.IO) {
            val existing = historyDao.getByUri(video.uriString)
            val entity = VideoHistoryEntity(
                uriString = video.uriString,
                title = video.title,
                durationMs = video.durationMs,
                lastPositionMs = positionMs,
                lastPlayedTimestamp = System.currentTimeMillis(),
                isFavorite = existing?.isFavorite ?: video.isFavorite,
                resolutionLabel = video.resolutionLabel,
                codec = video.codec,
                audioCodec = video.audioChannels
            )
            historyDao.insertOrUpdate(entity)
        }
    }

    suspend fun toggleFavorite(uriString: String, currentFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            historyDao.updateFavorite(uriString, !currentFavorite)
        }
    }

    suspend fun queryDeviceVideos(): List<VideoItem> {
        return withContext(Dispatchers.IO) {
            val videoList = mutableListOf<VideoItem>()
            val contentResolver: ContentResolver = context.contentResolver
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }

            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.WIDTH,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.SIZE
            )

            try {
                val cursor: Cursor? = contentResolver.query(
                    collection,
                    projection,
                    null,
                    null,
                    "${MediaStore.Video.Media.DATE_ADDED} DESC"
                )

                cursor?.use {
                    val idCol = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                    val nameCol = it.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                    val durationCol = it.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                    val widthCol = it.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)
                    val heightCol = it.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)
                    val sizeCol = it.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)

                    while (it.moveToNext()) {
                        val id = it.getLong(idCol)
                        val name = it.getString(nameCol) ?: "Video_$id"
                        val duration = it.getLong(durationCol)
                        val width = it.getInt(widthCol)
                        val height = it.getInt(heightCol)
                        val size = it.getLong(sizeCol)
                        val contentUri = Uri.withAppendedPath(collection, id.toString()).toString()

                        val resLabel = when {
                            width >= 7000 || height >= 4000 -> "8K UHD"
                            width >= 3600 || height >= 2000 -> "4K UHD"
                            width >= 2400 || height >= 1400 -> "2K QHD"
                            width >= 1800 || height >= 1000 -> "1080p FHD"
                            else -> "720p HD"
                        }

                        val aiScene = if (width >= 3600) "4K Ultra Master Scene" else "Local Media Stream"

                        videoList.add(
                            VideoItem(
                                id = id.toString(),
                                title = name,
                                uriString = contentUri,
                                durationMs = duration,
                                resolutionLabel = resLabel,
                                width = if (width > 0) width else 1920,
                                height = if (height > 0) height else 1080,
                                fps = 60,
                                codec = if (resLabel.contains("4K") || resLabel.contains("8K")) "HEVC / H.265" else "H.264",
                                hdrType = if (resLabel.contains("8K")) "HDR10+" else "SDR BT.709",
                                audioChannels = "Stereo Dolby AAC",
                                sizeBytes = size,
                                posterResId = null,
                                isDemo = false,
                                aiSceneTag = aiScene,
                                aiRecommendation = "HW Acceleration Active"
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // If permission is not yet granted or cursor failed, return safely
            }
            videoList
        }
    }

    suspend fun createVideoFromUri(uri: Uri, title: String? = null): VideoItem {
        return withContext(Dispatchers.IO) {
            val displayName = title ?: (uri.lastPathSegment ?: "External Video")
            val savedPos = getSavedPosition(uri.toString())
            VideoItem(
                id = "custom_${System.currentTimeMillis()}",
                title = displayName,
                uriString = uri.toString(),
                durationMs = 0L,
                resolutionLabel = "4K / HD Auto",
                width = 3840,
                height = 2160,
                fps = 60,
                codec = "HEVC / HW+ Auto",
                hdrType = "HDR / SDR",
                audioChannels = "Multi-channel Auto",
                sizeBytes = 0L,
                posterResId = null,
                isDemo = false,
                lastPositionMs = savedPos,
                aiSceneTag = "Local Ultra HD Content",
                aiRecommendation = "HW+ Auto Decoder Active"
            )
        }
    }
}
