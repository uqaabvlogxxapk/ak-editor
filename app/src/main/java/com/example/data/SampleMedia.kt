package com.example.data

import com.example.model.ClipType

data class SampleMediaItem(
  val id: String,
  val title: String,
  val type: ClipType,
  val durationMs: Long,
  val description: String,
  val dominantColorHex: String,
  val secondaryColorHex: String
)

object SampleMediaLibrary {
  val videoItems = listOf(
    SampleMediaItem(
      id = "cyberpunk_city",
      title = "Cyberpunk Neo Tokyo",
      type = ClipType.VIDEO,
      durationMs = 8000L,
      description = "Night aerial skyline with vibrant purple & cyan billboards and neon highways",
      dominantColorHex = "#8B5CF6",
      secondaryColorHex = "#06B6D4"
    ),
    SampleMediaItem(
      id = "mountain_drone",
      title = "Alpine Mountain Ridge",
      type = ClipType.VIDEO,
      durationMs = 10000L,
      description = "High altitude drone pass over snow-capped peaks and evergreen pines",
      dominantColorHex = "#0284C7",
      secondaryColorHex = "#0D9488"
    ),
    SampleMediaItem(
      id = "sunset_highway",
      title = "California Coastal Sunset",
      type = ClipType.VIDEO,
      durationMs = 9000L,
      description = "Warm golden hour drive along ocean cliffs with sunset reflections",
      dominantColorHex = "#F59E0B",
      secondaryColorHex = "#EF4444"
    ),
    SampleMediaItem(
      id = "waterfall_canyon",
      title = "Emerald Waterfall Canyon",
      type = ClipType.VIDEO,
      durationMs = 7000L,
      description = "Deep forest rushing water cascade with misty morning sunbeams",
      dominantColorHex = "#10B981",
      secondaryColorHex = "#059669"
    ),
    SampleMediaItem(
      id = "studio_action",
      title = "Studio Green Screen Dancer",
      type = ClipType.VIDEO,
      durationMs = 8000L,
      description = "Subject in studio with bright green chroma background ready for keying",
      dominantColorHex = "#22C55E",
      secondaryColorHex = "#15803D"
    ),
    SampleMediaItem(
      id = "skate_park",
      title = "Urban Skate Freestyle",
      type = ClipType.VIDEO,
      durationMs = 6000L,
      description = "Action skate trick session under bridge with gritty concrete textures",
      dominantColorHex = "#64748B",
      secondaryColorHex = "#334155"
    )
  )

  val audioItems = listOf(
    SampleMediaItem(
      id = "audio_cinematic",
      title = "Interstellar Horizons (Ambient)",
      type = ClipType.AUDIO,
      durationMs = 25000L,
      description = "Warm analog synth pads and cinematic sub-bass pulse (95 BPM)",
      dominantColorHex = "#8B5CF6",
      secondaryColorHex = "#3B82F6"
    ),
    SampleMediaItem(
      id = "audio_phonk",
      title = "Night Drive Drift (Phonk Beat)",
      type = ClipType.AUDIO,
      durationMs = 18000L,
      description = "Aggressive 808 cowbell bounce with vinyl hiss (130 BPM)",
      dominantColorHex = "#EC4899",
      secondaryColorHex = "#F43F5E"
    ),
    SampleMediaItem(
      id = "audio_lofi",
      title = "Midnight Coffee (Lo-Fi Chill)",
      type = ClipType.AUDIO,
      durationMs = 20000L,
      description = "Mellow Rhodes piano with rain ambience and dusty drums (80 BPM)",
      dominantColorHex = "#F59E0B",
      secondaryColorHex = "#D97706"
    ),
    SampleMediaItem(
      id = "audio_heavy_bass",
      title = "Cyber Warfare (Glitch Bass)",
      type = ClipType.AUDIO,
      durationMs = 15000L,
      description = "Heavy electronic drops with stuttered sidechain compression",
      dominantColorHex = "#06B6D4",
      secondaryColorHex = "#3B82F6"
    ),
    SampleMediaItem(
      id = "audio_hit",
      title = "Cinematic Impact & Whoosh",
      type = ClipType.AUDIO,
      durationMs = 8000L,
      description = "Modern trailer braam with high energy sub rumble",
      dominantColorHex = "#6366F1",
      secondaryColorHex = "#4F46E5"
    ),
    SampleMediaItem(
      id = "audio_piano",
      title = "Golden Hour Piano Suite",
      type = ClipType.AUDIO,
      durationMs = 22000L,
      description = "Intimate upright acoustic piano with ambient room reverb",
      dominantColorHex = "#FCD34D",
      secondaryColorHex = "#F59E0B"
    )
  )
}
