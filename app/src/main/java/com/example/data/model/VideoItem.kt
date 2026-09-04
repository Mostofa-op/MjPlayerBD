package com.example.data.model

data class VideoItem(
    val id: String,
    val title: String,
    val uriString: String,
    val durationMs: Long = 0L,
    val resolutionLabel: String = "4K UHD",
    val width: Int = 3840,
    val height: Int = 2160,
    val fps: Int = 60,
    val codec: String = "HEVC / H.265",
    val hdrType: String = "HDR10+",
    val audioChannels: String = "Dolby Atmos 7.1",
    val sizeBytes: Long = 0L,
    val posterResId: Int? = null,
    val isDemo: Boolean = false,
    val isFavorite: Boolean = false,
    val lastPositionMs: Long = 0L,
    val lastPlayedTimestamp: Long = System.currentTimeMillis(),
    val aiSceneTag: String = "Cinematic Visuals",
    val aiRecommendation: String = "HW+ Ultra Acceleration Recommended"
) {
    val progressFraction: Float
        get() = if (durationMs > 0L) (lastPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f

    fun formattedDuration(): String {
        if (durationMs <= 0L) return "Live / Stream"
        val totalSecs = durationMs / 1000
        val hours = totalSecs / 3600
        val mins = (totalSecs % 3600) / 60
        val secs = totalSecs % 60
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, mins, secs)
        } else {
            String.format("%02d:%02d", mins, secs)
        }
    }
}
