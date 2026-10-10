package com.example.model

import com.squareup.moshi.JsonClass
import java.util.UUID

@JsonClass(generateAdapter = true)
data class UserProfile(
  val id: String = "",
  val email: String = "",
  val displayName: String = "",
  val isPro: Boolean = false,
  val createdAt: String = ""
) {
  fun toMap(): Map<String, Any> = mapOf(
    "id" to id,
    "email" to email,
    "displayName" to displayName,
    "isPro" to isPro,
    "createdAt" to createdAt
  )
}

enum class AspectRatio(val label: String, val ratio: Float, val iconDescription: String) {
  RATIO_16_9("16:9", 16f / 9f, "Landscape / YouTube"),
  RATIO_9_16("9:16", 9f / 16f, "Vertical / Reels / TikTok"),
  RATIO_1_1("1:1", 1f, "Square / Instagram Post"),
  RATIO_4_5("4:5", 4f / 5f, "Portrait / Feed")
}

enum class ExportResolution(val label: String, val width: Int, val height: Int, val isPro: Boolean) {
  RES_720P("720p HD", 1280, 720, false),
  RES_1080P("1080p Full HD", 1920, 1080, false),
  RES_2K("1440p 2K QHD", 2560, 1440, true),
  RES_4K("2160p 4K UHD", 3840, 2160, true)
}

enum class ExportFps(val fps: Int, val label: String) {
  FPS_24(24, "24 FPS (Cinematic)"),
  FPS_30(30, "30 FPS (Standard)"),
  FPS_60(60, "60 FPS (Ultra Smooth)")
}

enum class ClipType {
  VIDEO,
  IMAGE,
  AUDIO
}

enum class ColorFilterType(val displayName: String) {
  NONE("Normal"),
  CINEMATIC("Cinematic Teal"),
  TEAL_ORANGE("Teal & Orange"),
  VINTAGE_90S("Vintage 90s"),
  FILM_NOIR("Film Noir (B&W)"),
  GOLDEN_HOUR("Golden Hour"),
  EMERALD("Emerald Mood"),
  GLITCH("Glitch Neon"),
  FILM_GRAIN("35mm Film Grain")
}

enum class TransitionType(val displayName: String) {
  NONE("None"),
  FADE("Fade to Black"),
  CROSS_DISSOLVE("Cross Dissolve"),
  WIPE_LEFT("Wipe Left"),
  SLIDE_UP("Slide Up"),
  ZOOM_IN("Zoom In")
}

enum class MaskType(val displayName: String) {
  NONE("No Mask"),
  RECTANGLE("Rectangle Mask"),
  ELLIPSE("Circular Mask"),
  SPLIT("Horizontal Split"),
  RADIAL("Radial Spotlight")
}

enum class TextAnimationType(val displayName: String) {
  NONE("Static"),
  FADE_IN("Fade In"),
  SLIDE_RIGHT("Slide In"),
  SCALE_POP("Pop In"),
  TYPEWRITER("Typewriter"),
  NEON_PULSE("Neon Pulse")
}

enum class SubtitleStyle(val displayName: String) {
  CLASSIC("Classic White"),
  NEON_GLOW("Neon Cyan Glow"),
  KARAOKE("Karaoke Yellow"),
  TYPEWRITER("Minimal Terminal"),
  YELLOW_BOX("Yellow Block Box"),
  BOLD_SANS("Bold Headline")
}

@JsonClass(generateAdapter = true)
data class TrackingKeyframe(
  val timestampMs: Long = 0L,
  val xNorm: Float = 0.5f,
  val yNorm: Float = 0.5f
)

@JsonClass(generateAdapter = true)
data class MotionTrackingData(
  val isEnabled: Boolean = false,
  val targetLabel: String = "Subject",
  val keyframes: List<TrackingKeyframe> = emptyList()
)

@JsonClass(generateAdapter = true)
data class TimelineClip(
  val id: String = UUID.randomUUID().toString(),
  val name: String = "Clip",
  val type: ClipType = ClipType.VIDEO,
  val trackIndex: Int = 0, // 0: Video Track 1, 1: Overlay Track 2, 2: Audio Track 3
  val startTimeMs: Long = 0L,
  val durationMs: Long = 5000L,
  val sourceDurationMs: Long = 5000L,
  val trimInMs: Long = 0L,
  val trimOutMs: Long = 0L,
  val volume: Float = 1.0f,
  val fadeInMs: Long = 0L,
  val fadeOutMs: Long = 0L,
  val colorFilter: ColorFilterType = ColorFilterType.NONE,
  val brightness: Float = 0f, // -1.0 to 1.0
  val contrast: Float = 1.0f, // 0.5 to 2.0
  val saturation: Float = 1.0f, // 0.0 to 2.0
  val temperature: Float = 0f, // -1.0 (cool) to 1.0 (warm)
  val vignette: Float = 0f, // 0.0 to 1.0
  val chromaKeyEnabled: Boolean = false,
  val chromaKeyColorHex: String = "#00FF00",
  val chromaKeyTolerance: Float = 0.4f,
  val chromaKeySoftness: Float = 0.1f,
  val maskType: MaskType = MaskType.NONE,
  val maskScale: Float = 1.0f,
  val maskFeather: Float = 10f,
  val maskInverted: Boolean = false,
  val transitionType: TransitionType = TransitionType.NONE,
  val transitionDurationMs: Long = 500L,
  val sampleAssetId: String? = null,
  val localUri: String? = null
)

@JsonClass(generateAdapter = true)
data class TextOverlay(
  val id: String = UUID.randomUUID().toString(),
  val text: String = "AK EDITOR",
  val startTimeMs: Long = 0L,
  val durationMs: Long = 3000L,
  val fontSize: Int = 24,
  val textColorHex: String = "#FFFFFF",
  val hasBackground: Boolean = false,
  val backgroundColorHex: String = "#80000000",
  val hasStroke: Boolean = true,
  val strokeColorHex: String = "#000000",
  val positionX: Float = 0.5f,
  val positionY: Float = 0.7f,
  val animationType: TextAnimationType = TextAnimationType.FADE_IN,
  val trackedToMotion: Boolean = false
)

@JsonClass(generateAdapter = true)
data class SubtitleSegment(
  val id: String = UUID.randomUUID().toString(),
  val text: String = "Caption",
  val startTimeMs: Long = 0L,
  val endTimeMs: Long = 2000L,
  val style: SubtitleStyle = SubtitleStyle.NEON_GLOW
)

@JsonClass(generateAdapter = true)
data class VideoProject(
  val id: String = UUID.randomUUID().toString(),
  val userId: String = "",
  val title: String = "New Cinematic Project",
  val aspectRatio: AspectRatio = AspectRatio.RATIO_16_9,
  val resolution: ExportResolution = ExportResolution.RES_1080P,
  val fps: ExportFps = ExportFps.FPS_30,
  val durationMs: Long = 15000L,
  val clips: List<TimelineClip> = emptyList(),
  val textOverlays: List<TextOverlay> = emptyList(),
  val subtitles: List<SubtitleSegment> = emptyList(),
  val motionTracking: MotionTrackingData = MotionTrackingData(),
  val watermarkEnabled: Boolean = false,
  val customWatermarkText: String = "AK EDITOR",
  val createdAt: String = "",
  val updatedAt: String = ""
)
