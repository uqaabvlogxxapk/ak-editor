package com.example.model

data class VideoTemplate(
  val id: String,
  val name: String,
  val category: String,
  val description: String,
  val aspectRatio: AspectRatio,
  val durationMs: Long,
  val isPro: Boolean,
  val tags: List<String>,
  val createProject: () -> VideoProject
)

object TemplateLibrary {
  val templates: List<VideoTemplate> = listOf(
    VideoTemplate(
      id = "tmpl_cinematic_travel",
      name = "Cinematic Odyssey",
      category = "Cinematic",
      description = "Widescreen anamorphic color grading with smooth dissolves, letterbox bars, and minimalist titles.",
      aspectRatio = AspectRatio.RATIO_16_9,
      durationMs = 18000L,
      isPro = false,
      tags = listOf("4K Ready", "Teal & Orange", "Letterbox"),
      createProject = {
        VideoProject(
          title = "Cinematic Odyssey",
          aspectRatio = AspectRatio.RATIO_16_9,
          resolution = ExportResolution.RES_1080P,
          fps = ExportFps.FPS_24,
          durationMs = 18000L,
          clips = listOf(
            TimelineClip(
              name = "Alpine Mountain Drone",
              type = ClipType.VIDEO,
              trackIndex = 0,
              startTimeMs = 0L,
              durationMs = 6000L,
              colorFilter = ColorFilterType.CINEMATIC,
              contrast = 1.15f,
              temperature = -0.1f,
              sampleAssetId = "mountain_drone",
              transitionType = TransitionType.CROSS_DISSOLVE
            ),
            TimelineClip(
              name = "Sunset Coastal Highway",
              type = ClipType.VIDEO,
              trackIndex = 0,
              startTimeMs = 6000L,
              durationMs = 6000L,
              colorFilter = ColorFilterType.GOLDEN_HOUR,
              temperature = 0.3f,
              sampleAssetId = "sunset_highway",
              transitionType = TransitionType.FADE
            ),
            TimelineClip(
              name = "Misty Waterfall Canyon",
              type = ClipType.VIDEO,
              trackIndex = 0,
              startTimeMs = 12000L,
              durationMs = 6000L,
              colorFilter = ColorFilterType.EMERALD,
              saturation = 1.2f,
              sampleAssetId = "waterfall_canyon"
            ),
            TimelineClip(
              name = "Cinematic Ambient Track",
              type = ClipType.AUDIO,
              trackIndex = 2,
              startTimeMs = 0L,
              durationMs = 18000L,
              volume = 0.85f,
              fadeInMs = 1000L,
              fadeOutMs = 1500L,
              sampleAssetId = "audio_cinematic"
            )
          ),
          textOverlays = listOf(
            TextOverlay(
              text = "INTO THE HORIZON",
              startTimeMs = 500L,
              durationMs = 4500L,
              fontSize = 32,
              textColorHex = "#FFFFFF",
              positionX = 0.5f,
              positionY = 0.5f,
              animationType = TextAnimationType.FADE_IN
            ),
            TextOverlay(
              text = "A FILM BY AK EDITOR",
              startTimeMs = 13000L,
              durationMs = 4000L,
              fontSize = 20,
              textColorHex = "#94A3B8",
              positionX = 0.5f,
              positionY = 0.8f,
              animationType = TextAnimationType.FADE_IN
            )
          )
        )
      }
    ),
    VideoTemplate(
      id = "tmpl_reels_viral",
      name = "Viral Reels Flashcut",
      category = "Social Media Reels",
      description = "Vertical high-energy cuts with dynamic neon auto-captions and fast zoom transitions.",
      aspectRatio = AspectRatio.RATIO_9_16,
      durationMs = 12000L,
      isPro = false,
      tags = listOf("9:16 Vertical", "Captions", "Fast Pace"),
      createProject = {
        VideoProject(
          title = "Viral Reels Flashcut",
          aspectRatio = AspectRatio.RATIO_9_16,
          resolution = ExportResolution.RES_1080P,
          fps = ExportFps.FPS_30,
          durationMs = 12000L,
          clips = listOf(
            TimelineClip(
              name = "Cyberpunk City Pulse",
              type = ClipType.VIDEO,
              trackIndex = 0,
              startTimeMs = 0L,
              durationMs = 4000L,
              colorFilter = ColorFilterType.GLITCH,
              sampleAssetId = "cyberpunk_city",
              transitionType = TransitionType.ZOOM_IN
            ),
            TimelineClip(
              name = "Neon Street Walk",
              type = ClipType.VIDEO,
              trackIndex = 0,
              startTimeMs = 4000L,
              durationMs = 4000L,
              colorFilter = ColorFilterType.TEAL_ORANGE,
              sampleAssetId = "neon_street",
              transitionType = TransitionType.WIPE_LEFT
            ),
            TimelineClip(
              name = "Studio Dancer Action",
              type = ClipType.VIDEO,
              trackIndex = 0,
              startTimeMs = 8000L,
              durationMs = 4000L,
              colorFilter = ColorFilterType.NONE,
              sampleAssetId = "studio_action"
            ),
            TimelineClip(
              name = "Phonk Trap Beat",
              type = ClipType.AUDIO,
              trackIndex = 2,
              startTimeMs = 0L,
              durationMs = 12000L,
              volume = 1.0f,
              sampleAssetId = "audio_phonk"
            )
          ),
          subtitles = listOf(
            SubtitleSegment(text = "DON'T MISS THIS", startTimeMs = 500L, endTimeMs = 2500L, style = SubtitleStyle.NEON_GLOW),
            SubtitleSegment(text = "EDITED WITH AK EDITOR", startTimeMs = 3000L, endTimeMs = 6000L, style = SubtitleStyle.KARAOKE),
            SubtitleSegment(text = "TAP TO TRY NOW 🔥", startTimeMs = 7000L, endTimeMs = 11000L, style = SubtitleStyle.YELLOW_BOX)
          )
        )
      }
    ),
    VideoTemplate(
      id = "tmpl_gaming_frag",
      name = "Esports Frag Montage",
      category = "Gaming",
      description = "Electric neon overlays, aggressive speed ramps, glitch transitions, and bass boosted punch.",
      aspectRatio = AspectRatio.RATIO_16_9,
      durationMs = 15000L,
      isPro = true,
      tags = listOf("60 FPS", "Pro FX", "Glitch"),
      createProject = {
        VideoProject(
          title = "Esports Frag Montage",
          aspectRatio = AspectRatio.RATIO_16_9,
          resolution = ExportResolution.RES_2K,
          fps = ExportFps.FPS_60,
          durationMs = 15000L,
          clips = listOf(
            TimelineClip(
              name = "Cyberpunk Alley Rush",
              type = ClipType.VIDEO,
              trackIndex = 0,
              startTimeMs = 0L,
              durationMs = 5000L,
              colorFilter = ColorFilterType.GLITCH,
              vignette = 0.4f,
              sampleAssetId = "cyberpunk_city",
              transitionType = TransitionType.SLIDE_UP
            ),
            TimelineClip(
              name = "Skate Park Trick",
              type = ClipType.VIDEO,
              trackIndex = 0,
              startTimeMs = 5000L,
              durationMs = 5000L,
              colorFilter = ColorFilterType.FILM_GRAIN,
              contrast = 1.3f,
              sampleAssetId = "skate_park",
              transitionType = TransitionType.ZOOM_IN
            ),
            TimelineClip(
              name = "Sunset Drift",
              type = ClipType.VIDEO,
              trackIndex = 0,
              startTimeMs = 10000L,
              durationMs = 5000L,
              colorFilter = ColorFilterType.TEAL_ORANGE,
              sampleAssetId = "sunset_highway"
            ),
            TimelineClip(
              name = "Heavy Glitch Bass Track",
              type = ClipType.AUDIO,
              trackIndex = 2,
              startTimeMs = 0L,
              durationMs = 15000L,
              volume = 1.2f,
              sampleAssetId = "audio_heavy_bass"
            )
          ),
          textOverlays = listOf(
            TextOverlay(
              text = "UNSTOPPABLE",
              startTimeMs = 1000L,
              durationMs = 3000L,
              fontSize = 38,
              textColorHex = "#06B6D4",
              hasStroke = true,
              strokeColorHex = "#8B5CF6",
              animationType = TextAnimationType.SCALE_POP
            )
          )
        )
      }
    ),
    VideoTemplate(
      id = "tmpl_product_promo",
      name = "Minimalist Product Promo",
      category = "Product Advertisements",
      description = "Clean square 1:1 format for e-commerce, sleek lower thirds, and smooth motion typography.",
      aspectRatio = AspectRatio.RATIO_1_1,
      durationMs = 10000L,
      isPro = false,
      tags = listOf("1:1 Square", "Clean Commercial", "E-commerce"),
      createProject = {
        VideoProject(
          title = "Minimalist Product Promo",
          aspectRatio = AspectRatio.RATIO_1_1,
          resolution = ExportResolution.RES_1080P,
          fps = ExportFps.FPS_30,
          durationMs = 10000L,
          clips = listOf(
            TimelineClip(
              name = "Studio Product Display",
              type = ClipType.VIDEO,
              trackIndex = 0,
              startTimeMs = 0L,
              durationMs = 5000L,
              colorFilter = ColorFilterType.NONE,
              contrast = 1.1f,
              sampleAssetId = "studio_action",
              transitionType = TransitionType.CROSS_DISSOLVE
            ),
            TimelineClip(
              name = "Alpine Mountain Texture",
              type = ClipType.VIDEO,
              trackIndex = 0,
              startTimeMs = 5000L,
              durationMs = 5000L,
              colorFilter = ColorFilterType.CINEMATIC,
              sampleAssetId = "mountain_drone"
            ),
            TimelineClip(
              name = "Modern Lo-Fi Beats",
              type = ClipType.AUDIO,
              trackIndex = 2,
              startTimeMs = 0L,
              durationMs = 10000L,
              volume = 0.9f,
              sampleAssetId = "audio_lofi"
            )
          ),
          textOverlays = listOf(
            TextOverlay(
              text = "THE NEW STANDARD",
              startTimeMs = 500L,
              durationMs = 4000L,
              fontSize = 26,
              textColorHex = "#FFFFFF",
              hasBackground = true,
              backgroundColorHex = "#CC161329",
              positionX = 0.5f,
              positionY = 0.25f,
              animationType = TextAnimationType.SLIDE_RIGHT
            ),
            TextOverlay(
              text = "UP TO 40% OFF THIS WEEK",
              startTimeMs = 5500L,
              durationMs = 4000L,
              fontSize = 22,
              textColorHex = "#06B6D4",
              hasBackground = true,
              backgroundColorHex = "#CC0D0B18",
              positionX = 0.5f,
              positionY = 0.75f,
              animationType = TextAnimationType.SCALE_POP
            )
          )
        )
      }
    ),
    VideoTemplate(
      id = "tmpl_youtube_intro",
      name = "YouTube Cyber Intro",
      category = "YouTube Intros",
      description = "Punchy 8-second animated logo reveal with sound effects and neon accents.",
      aspectRatio = AspectRatio.RATIO_16_9,
      durationMs = 8000L,
      isPro = false,
      tags = listOf("Intro", "Logo Reveal", "Sound FX"),
      createProject = {
        VideoProject(
          title = "YouTube Cyber Intro",
          aspectRatio = AspectRatio.RATIO_16_9,
          resolution = ExportResolution.RES_1080P,
          fps = ExportFps.FPS_30,
          durationMs = 8000L,
          clips = listOf(
            TimelineClip(
              name = "Cyber City Glitch",
              type = ClipType.VIDEO,
              trackIndex = 0,
              startTimeMs = 0L,
              durationMs = 8000L,
              colorFilter = ColorFilterType.GLITCH,
              vignette = 0.3f,
              sampleAssetId = "cyberpunk_city"
            ),
            TimelineClip(
              name = "Electronic Whoosh & Hit",
              type = ClipType.AUDIO,
              trackIndex = 2,
              startTimeMs = 0L,
              durationMs = 8000L,
              volume = 1.0f,
              sampleAssetId = "audio_hit"
            )
          ),
          textOverlays = listOf(
            TextOverlay(
              text = "AK EDITOR",
              startTimeMs = 800L,
              durationMs = 6000L,
              fontSize = 42,
              textColorHex = "#8B5CF6",
              hasStroke = true,
              strokeColorHex = "#06B6D4",
              positionX = 0.5f,
              positionY = 0.45f,
              animationType = TextAnimationType.NEON_PULSE
            ),
            TextOverlay(
              text = "SUBSCRIBE FOR MORE",
              startTimeMs = 2500L,
              durationMs = 4500L,
              fontSize = 18,
              textColorHex = "#F8FAFC",
              positionX = 0.5f,
              positionY = 0.65f,
              animationType = TextAnimationType.TYPEWRITER
            )
          )
        )
      }
    ),
    VideoTemplate(
      id = "tmpl_wedding_memory",
      name = "Eternal Wedding Memories",
      category = "Wedding",
      description = "Romantic soft glow grading, slow cross dissolves, and elegant golden typography.",
      aspectRatio = AspectRatio.RATIO_16_9,
      durationMs = 20000L,
      isPro = true,
      tags = listOf("Romantic", "Golden Glow", "4K Ready"),
      createProject = {
        VideoProject(
          title = "Eternal Wedding Memories",
          aspectRatio = AspectRatio.RATIO_16_9,
          resolution = ExportResolution.RES_4K,
          fps = ExportFps.FPS_24,
          durationMs = 20000L,
          clips = listOf(
            TimelineClip(
              name = "Sunset Horizon",
              type = ClipType.VIDEO,
              trackIndex = 0,
              startTimeMs = 0L,
              durationMs = 10000L,
              colorFilter = ColorFilterType.GOLDEN_HOUR,
              temperature = 0.4f,
              sampleAssetId = "sunset_highway",
              transitionType = TransitionType.CROSS_DISSOLVE
            ),
            TimelineClip(
              name = "Gentle Mountain Breeze",
              type = ClipType.VIDEO,
              trackIndex = 0,
              startTimeMs = 10000L,
              durationMs = 10000L,
              colorFilter = ColorFilterType.CINEMATIC,
              sampleAssetId = "mountain_drone"
            ),
            TimelineClip(
              name = "Romantic Piano & Strings",
              type = ClipType.AUDIO,
              trackIndex = 2,
              startTimeMs = 0L,
              durationMs = 20000L,
              volume = 0.9f,
              fadeInMs = 1500L,
              fadeOutMs = 2000L,
              sampleAssetId = "audio_piano"
            )
          ),
          textOverlays = listOf(
            TextOverlay(
              text = "TOGETHER FOREVER",
              startTimeMs = 1000L,
              durationMs = 6000L,
              fontSize = 30,
              textColorHex = "#FEF08A",
              positionX = 0.5f,
              positionY = 0.5f,
              animationType = TextAnimationType.FADE_IN
            )
          )
        )
      }
    )
  )
}
