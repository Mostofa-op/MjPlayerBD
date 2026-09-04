package com.example.ai

import androidx.media3.common.Format
import com.example.data.model.VideoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AiVideoTelemetry(
    val resolutionLabel: String = "4K UHD",
    val actualWidth: Int = 3840,
    val actualHeight: Int = 2160,
    val fps: Int = 60,
    val codec: String = "HEVC / H.265",
    val colorSpace: String = "BT.2020 (HDR10+)",
    val bitrateKbps: Int = 45200,
    val audioFormat: String = "Dolby Atmos 7.1",
    val decoderEngine: String = "Qualcomm HW+ Accelerated",
    val detectedScene: String = "Cinematic High Dynamic Range",
    val confidenceScore: Float = 0.98f,
    val latencyMs: Int = 0,
    val droppedFrames: Int = 0
)

data class AiEnhancementSettings(
    val crystalClarityEnabled: Boolean = true,
    val crystalClarityIntensity: Float = 0.75f,
    val hdrVividEnabled: Boolean = true,
    val hdrVividIntensity: Float = 0.85f,
    val nightVisionEnabled: Boolean = false,
    val nightVisionIntensity: Float = 0.5f,
    val vocalBoostEnabled: Boolean = true,
    val motionSmootherEnabled: Boolean = true,
    val superResolution8kSimulation: Boolean = false
)

class AiVideoEngine {
    private val _telemetry = MutableStateFlow(AiVideoTelemetry())
    val telemetry: StateFlow<AiVideoTelemetry> = _telemetry.asStateFlow()

    private val _enhancements = MutableStateFlow(AiEnhancementSettings())
    val enhancements: StateFlow<AiEnhancementSettings> = _enhancements.asStateFlow()

    fun updateFromFormat(format: Format?, fallbackVideo: VideoItem?) {
        val width = format?.width ?: fallbackVideo?.width ?: 3840
        val height = format?.height ?: fallbackVideo?.height ?: 2160
        val fps = (format?.frameRate ?: fallbackVideo?.fps?.toFloat() ?: 60f).toInt()
        val bitrate = if (format?.bitrate != null && format.bitrate > 0) format.bitrate / 1000 else 42000

        val resLabel = when {
            width >= 7000 || height >= 4000 -> "8K ULTRA HD"
            width >= 3500 || height >= 2000 -> "4K UHD"
            width >= 2400 || height >= 1400 -> "2K QHD"
            width >= 1800 || height >= 1000 -> "1080p FHD"
            else -> "720p HD"
        }

        val codecName = when {
            format?.sampleMimeType?.contains("av01") == true -> "AV1 Next-Gen Codec"
            format?.sampleMimeType?.contains("hevc") == true -> "HEVC / H.265 Main 10"
            format?.sampleMimeType?.contains("vp9") == true -> "Google VP9 Profile 2"
            format?.sampleMimeType?.contains("avc") == true -> "H.264 / AVC High Profile"
            else -> fallbackVideo?.codec ?: "HEVC / H.265 Ultra"
        }

        val hdrLabel = when {
            width >= 7000 -> "Dolby Vision / BT.2020 12-bit"
            width >= 3500 -> "HDR10+ Dynamic BT.2020"
            else -> "SDR Rec.709 Standard"
        }

        val scene = when {
            fallbackVideo?.title?.contains("Cosmic", ignoreCase = true) == true -> "Cosmic Nebula & Astrophotography"
            fallbackVideo?.title?.contains("Cyberpunk", ignoreCase = true) == true -> "High-Contrast Neon Nightscape"
            fallbackVideo?.title?.contains("Amazon", ignoreCase = true) == true -> "Vibrant Ecological Landscape"
            resLabel.contains("8K") -> "Ultra-Definition 8K Master Quality"
            else -> "Cinematic 4K Live Scene"
        }

        _telemetry.value = AiVideoTelemetry(
            resolutionLabel = resLabel,
            actualWidth = width,
            actualHeight = height,
            fps = if (fps > 0) fps else 60,
            codec = codecName,
            colorSpace = hdrLabel,
            bitrateKbps = bitrate,
            audioFormat = fallbackVideo?.audioChannels ?: "Dolby Digital Atmos 7.1",
            decoderEngine = "Zero-Lag HW+ Engine (Hardware Direct)",
            detectedScene = scene,
            confidenceScore = 0.99f,
            latencyMs = 2,
            droppedFrames = 0
        )
    }

    fun toggleCrystalClarity() {
        val cur = _enhancements.value
        _enhancements.value = cur.copy(crystalClarityEnabled = !cur.crystalClarityEnabled)
    }

    fun toggleHdrVivid() {
        val cur = _enhancements.value
        _enhancements.value = cur.copy(hdrVividEnabled = !cur.hdrVividEnabled)
    }

    fun toggleNightVision() {
        val cur = _enhancements.value
        _enhancements.value = cur.copy(nightVisionEnabled = !cur.nightVisionEnabled)
    }

    fun toggleVocalBoost() {
        val cur = _enhancements.value
        _enhancements.value = cur.copy(vocalBoostEnabled = !cur.vocalBoostEnabled)
    }

    fun toggleMotionSmoother() {
        val cur = _enhancements.value
        _enhancements.value = cur.copy(motionSmootherEnabled = !cur.motionSmootherEnabled)
    }

    fun toggleSuperResolution8k() {
        val cur = _enhancements.value
        _enhancements.value = cur.copy(superResolution8kSimulation = !cur.superResolution8kSimulation)
    }
}
