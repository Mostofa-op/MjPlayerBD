package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_history")
data class VideoHistoryEntity(
    @PrimaryKey
    val uriString: String,
    val title: String,
    val durationMs: Long,
    val lastPositionMs: Long,
    val lastPlayedTimestamp: Long,
    val isFavorite: Boolean = false,
    val resolutionLabel: String = "4K UHD",
    val codec: String = "HEVC",
    val audioCodec: String = "AAC / Dolby"
)
