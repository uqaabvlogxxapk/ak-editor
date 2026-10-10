package com.example.ui.editor

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AspectRatio
import com.example.model.ClipType
import com.example.model.ColorFilterType
import com.example.model.MaskType
import com.example.model.SubtitleSegment
import com.example.model.SubtitleStyle
import com.example.model.TextAnimationType
import com.example.model.TextOverlay
import com.example.model.TimelineClip
import com.example.model.VideoProject
import com.example.ui.theme.AkBgDark
import com.example.ui.theme.AkCyanAccent
import com.example.ui.theme.AkPurplePrimary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun VideoPreviewCanvas(
  project: VideoProject,
  currentPlayheadMs: Long,
  isPlaying: Boolean,
  showBeforeAfterSplit: Boolean = false,
  modifier: Modifier = Modifier
) {
  val ratioMultiplier = project.aspectRatio.ratio

  // Find active video clips on Track 0 (Main) and Track 1 (Overlay)
  val activeMainClip = project.clips.firstOrNull {
    it.trackIndex == 0 && currentPlayheadMs >= it.startTimeMs && currentPlayheadMs < (it.startTimeMs + it.durationMs)
  }

  val activePipClip = project.clips.firstOrNull {
    it.trackIndex == 1 && currentPlayheadMs >= it.startTimeMs && currentPlayheadMs < (it.startTimeMs + it.durationMs)
  }

  // Active text overlays
  val activeTextOverlays = project.textOverlays.filter {
    currentPlayheadMs >= it.startTimeMs && currentPlayheadMs < (it.startTimeMs + it.durationMs)
  }

  // Active subtitle segment
  val activeSubtitle = project.subtitles.firstOrNull {
    currentPlayheadMs >= it.startTimeMs && currentPlayheadMs < it.endTimeMs
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .aspectRatio(ratioMultiplier)
      .clip(RoundedCornerShape(8.dp))
      .background(Color.Black)
      .border(1.dp, Color(0xFF262145), RoundedCornerShape(8.dp)),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height

      if (activeMainClip != null) {
        val clipElapsed = (currentPlayheadMs - activeMainClip.startTimeMs).coerceAtLeast(0L)
        val clipProgress = (clipElapsed.toFloat() / activeMainClip.durationMs.coerceAtLeast(1L)).coerceIn(0f, 1f)

        if (showBeforeAfterSplit) {
          // Left half: Raw before
          drawClipProcedural(
            clip = activeMainClip,
            progress = clipProgress,
            clipRect = Rect(0f, 0f, w / 2f, h),
            applyGrade = false
          )
          // Right half: Graded after
          drawClipProcedural(
            clip = activeMainClip,
            progress = clipProgress,
            clipRect = Rect(w / 2f, 0f, w, h),
            applyGrade = true
          )
          // Divider line
          drawLine(
            color = AkCyanAccent,
            start = Offset(w / 2f, 0f),
            end = Offset(w / 2f, h),
            strokeWidth = 3f
          )
        } else {
          // Full canvas graded
          drawClipProcedural(
            clip = activeMainClip,
            progress = clipProgress,
            clipRect = Rect(0f, 0f, w, h),
            applyGrade = true
          )
        }

        // Draw PIP overlay if present
        if (activePipClip != null) {
          val pipW = w * 0.35f
          val pipH = h * 0.35f
          val pipX = w * 0.60f
          val pipY = h * 0.08f
          drawRoundRect(
            color = Color(0xFF1E1B38),
            topLeft = Offset(pipX, pipY),
            size = Size(pipW, pipH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
          )
          drawRect(
            color = AkPurplePrimary,
            topLeft = Offset(pipX, pipY),
            size = Size(pipW, pipH),
            style = Stroke(width = 3f)
          )
        }
      } else {
        // Empty state / Black screen with guide
        drawRect(Color(0xFF0F0D1C))
        drawCircle(
          brush = Brush.radialGradient(
            listOf(AkPurplePrimary.copy(alpha = 0.25f), Color.Transparent),
            center = Offset(w / 2f, h / 2f),
            radius = w * 0.4f
          ),
          center = Offset(w / 2f, h / 2f),
          radius = w * 0.4f
        )
      }

      // Draw Motion Tracking Reticle if enabled
      if (project.motionTracking.isEnabled && project.motionTracking.keyframes.isNotEmpty()) {
        val totalMs = project.durationMs.coerceAtLeast(1L)
        val normTime = (currentPlayheadMs.toFloat() / totalMs).coerceIn(0f, 1f)
        val trackX = w * (0.3f + 0.4f * sin(normTime * 6.28f).toFloat())
        val trackY = h * (0.4f + 0.2f * cos(normTime * 6.28f).toFloat())

        // Tracking Target Reticle
        drawCircle(
          color = AkCyanAccent,
          radius = 28f,
          center = Offset(trackX, trackY),
          style = Stroke(width = 3f)
        )
        drawLine(
          color = AkCyanAccent,
          start = Offset(trackX - 40f, trackY),
          end = Offset(trackX + 40f, trackY),
          strokeWidth = 2f
        )
        drawLine(
          color = AkCyanAccent,
          start = Offset(trackX, trackY - 40f),
          end = Offset(trackX, trackY + 40f),
          strokeWidth = 2f
        )
      }

      // Draw Active Text Overlays
      activeTextOverlays.forEach { textOverlay ->
        val overlayElapsed = currentPlayheadMs - textOverlay.startTimeMs
        val overlayProgress = (overlayElapsed.toFloat() / textOverlay.durationMs.coerceAtLeast(1L)).coerceIn(0f, 1f)

        drawAnimatedText(
          overlay = textOverlay,
          progress = overlayProgress,
          canvasWidth = w,
          canvasHeight = h
        )
      }

      // Draw Active Subtitle Segment
      if (activeSubtitle != null) {
        val subElapsed = currentPlayheadMs - activeSubtitle.startTimeMs
        val subDuration = (activeSubtitle.endTimeMs - activeSubtitle.startTimeMs).coerceAtLeast(1L)
        val subProgress = (subElapsed.toFloat() / subDuration).coerceIn(0f, 1f)

        drawSubtitle(
          subtitle = activeSubtitle,
          progress = subProgress,
          canvasWidth = w,
          canvasHeight = h
        )
      }

      // Draw Watermark if enabled
      if (project.watermarkEnabled) {
        val paint = android.graphics.Paint().apply {
          color = android.graphics.Color.argb(140, 255, 255, 255)
          textSize = 28f
          isAntiAlias = true
          isFakeBoldText = true
        }
        drawContext.canvas.nativeCanvas.drawText(
          project.customWatermarkText.ifEmpty { "AK EDITOR" },
          w - 180f,
          h - 32f,
          paint
        )
      }
    }

    // Split screen indicators
    if (showBeforeAfterSplit) {
      Text(
        text = "RAW",
        color = Color.White.copy(alpha = 0.7f),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
          .align(Alignment.TopStart)
          .padding(8.dp)
          .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
          .padding(horizontal = 6.dp, vertical = 2.dp)
      )
      Text(
        text = "GRADED",
        color = AkCyanAccent,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(8.dp)
          .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
          .padding(horizontal = 6.dp, vertical = 2.dp)
      )
    }
  }
}

private fun DrawScope.drawClipProcedural(
  clip: TimelineClip,
  progress: Float,
  clipRect: Rect,
  applyGrade: Boolean
) {
  val baseColors = when (clip.sampleAssetId) {
    "cyberpunk_city" -> listOf(Color(0xFF2E0854), Color(0xFF0F172A), Color(0xFF06B6D4))
    "mountain_drone" -> listOf(Color(0xFF0C4A6E), Color(0xFF0369A1), Color(0xFF0D9488))
    "sunset_highway" -> listOf(Color(0xFF9A3412), Color(0xFFEA580C), Color(0xFFFBBF24))
    "waterfall_canyon" -> listOf(Color(0xFF064E3B), Color(0xFF047857), Color(0xFF10B981))
    "studio_action" -> if (clip.chromaKeyEnabled) {
      // Keyed out background shows studio background
      listOf(Color(0xFF18181B), Color(0xFF27272A), Color(0xFF3F3F46))
    } else {
      listOf(Color(0xFF14532D), Color(0xFF16A34A), Color(0xFF22C55E))
    }
    "skate_park" -> listOf(Color(0xFF1E293B), Color(0xFF334155), Color(0xFF475569))
    else -> listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF4338CA))
  }

  // Draw Background Gradient
  val motionOffset = progress * 60f
  drawRect(
    brush = Brush.linearGradient(
      colors = baseColors,
      start = Offset(clipRect.left + motionOffset, clipRect.top),
      end = Offset(clipRect.right, clipRect.bottom + motionOffset)
    ),
    topLeft = Offset(clipRect.left, clipRect.top),
    size = Size(clipRect.width, clipRect.height)
  )

  // Dynamic Scene Elements
  val cx = clipRect.left + clipRect.width / 2f
  val cy = clipRect.top + clipRect.height / 2f

  // Moving subject or cinematic sun/glow
  val sunY = cy - clipRect.height * 0.15f + sin(progress * 3.14f) * 20f
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(Color.White.copy(alpha = 0.5f), Color.Transparent),
      center = Offset(cx, sunY),
      radius = clipRect.width * 0.25f
    ),
    center = Offset(cx, sunY),
    radius = clipRect.width * 0.25f
  )

  // Foreground Subject Silhouette or Green Screen Actor
  if (clip.sampleAssetId == "studio_action" || clip.name.contains("Action", ignoreCase = true)) {
    // Subject silhouette
    val subjectPaint = android.graphics.Paint().apply {
      color = if (clip.chromaKeyEnabled) android.graphics.Color.WHITE else android.graphics.Color.BLACK
      isAntiAlias = true
    }
    drawCircle(
      color = if (clip.chromaKeyEnabled) Color(0xFF38BDF8) else Color(0xFF0F172A),
      radius = clipRect.height * 0.18f,
      center = Offset(cx, cy + 20f)
    )
  }

  if (applyGrade) {
    // Apply Color Filter Previews
    when (clip.colorFilter) {
      ColorFilterType.CINEMATIC -> {
        drawRect(
          brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF06B6D4).copy(alpha = 0.15f), Color(0xFFF59E0B).copy(alpha = 0.15f)),
            startY = clipRect.top,
            endY = clipRect.bottom
          ),
          topLeft = Offset(clipRect.left, clipRect.top),
          size = Size(clipRect.width, clipRect.height),
          blendMode = BlendMode.Overlay
        )
      }
      ColorFilterType.TEAL_ORANGE -> {
        drawRect(
          brush = Brush.horizontalGradient(
            colors = listOf(Color(0xFF0284C7).copy(alpha = 0.20f), Color(0xFFF97316).copy(alpha = 0.20f)),
            startX = clipRect.left,
            endX = clipRect.right
          ),
          topLeft = Offset(clipRect.left, clipRect.top),
          size = Size(clipRect.width, clipRect.height),
          blendMode = BlendMode.ColorDodge
        )
      }
      ColorFilterType.FILM_NOIR -> {
        drawRect(
          color = Color(0xFF1E293B).copy(alpha = 0.75f),
          topLeft = Offset(clipRect.left, clipRect.top),
          size = Size(clipRect.width, clipRect.height),
          blendMode = BlendMode.Saturation
        )
      }
      ColorFilterType.GOLDEN_HOUR -> {
        drawRect(
          color = Color(0xFFFBBF24).copy(alpha = 0.25f),
          topLeft = Offset(clipRect.left, clipRect.top),
          size = Size(clipRect.width, clipRect.height),
          blendMode = BlendMode.Color
        )
      }
      ColorFilterType.GLITCH -> {
        // Glitch scanlines
        var lineY = clipRect.top
        while (lineY < clipRect.bottom) {
          drawLine(
            color = Color(0xFF06B6D4).copy(alpha = 0.12f),
            start = Offset(clipRect.left, lineY),
            end = Offset(clipRect.right, lineY),
            strokeWidth = 2f
          )
          lineY += 12f
        }
      }
      ColorFilterType.EMERALD -> {
        drawRect(
          color = Color(0xFF059669).copy(alpha = 0.20f),
          topLeft = Offset(clipRect.left, clipRect.top),
          size = Size(clipRect.width, clipRect.height),
          blendMode = BlendMode.Hue
        )
      }
      ColorFilterType.FILM_GRAIN -> {
        // Film Grain overlay
        drawRect(
          brush = Brush.linearGradient(
            colors = listOf(Color.White.copy(alpha = 0.08f), Color.Black.copy(alpha = 0.08f)),
            start = Offset(clipRect.left, clipRect.top),
            end = Offset(clipRect.right, clipRect.bottom)
          ),
          topLeft = Offset(clipRect.left, clipRect.top),
          size = Size(clipRect.width, clipRect.height)
        )
      }
      else -> { /* None */ }
    }

    // Apply Vignette if specified
    if (clip.vignette > 0.05f) {
      drawRect(
        brush = Brush.radialGradient(
          colors = listOf(Color.Transparent, Color.Black.copy(alpha = (clip.vignette * 0.85f).coerceIn(0f, 0.95f))),
          center = Offset(cx, cy),
          radius = (clipRect.width / 2f) * 1.1f
        ),
        topLeft = Offset(clipRect.left, clipRect.top),
        size = Size(clipRect.width, clipRect.height)
      )
    }

    // Apply Layer Mask if selected
    if (clip.maskType != MaskType.NONE) {
      when (clip.maskType) {
        MaskType.RECTANGLE -> {
          val maskW = clipRect.width * 0.7f * clip.maskScale
          val maskH = clipRect.height * 0.7f * clip.maskScale
          drawRect(
            color = Color.Black.copy(alpha = 0.65f),
            topLeft = Offset(clipRect.left, clipRect.top),
            size = Size(clipRect.width, (clipRect.height - maskH) / 2f)
          )
          drawRect(
            color = Color.Black.copy(alpha = 0.65f),
            topLeft = Offset(clipRect.left, cy + maskH / 2f),
            size = Size(clipRect.width, (clipRect.height - maskH) / 2f)
          )
        }
        MaskType.ELLIPSE -> {
          drawCircle(
            color = AkCyanAccent,
            radius = (clipRect.width * 0.35f * clip.maskScale).coerceAtLeast(10f),
            center = Offset(cx, cy),
            style = Stroke(width = 3f)
          )
        }
        MaskType.SPLIT -> {
          drawLine(
            color = AkPurplePrimary,
            start = Offset(clipRect.left, cy),
            end = Offset(clipRect.right, cy),
            strokeWidth = 4f
          )
        }
        else -> {}
      }
    }
  }
}

private fun DrawScope.drawAnimatedText(
  overlay: TextOverlay,
  progress: Float,
  canvasWidth: Float,
  canvasHeight: Float
) {
  val baseColor = try {
    Color(android.graphics.Color.parseColor(overlay.textColorHex))
  } catch (e: Exception) {
    Color.White
  }

  val alpha = when (overlay.animationType) {
    TextAnimationType.FADE_IN -> (progress * 3f).coerceIn(0f, 1f)
    TextAnimationType.NEON_PULSE -> 0.7f + 0.3f * sin(progress * 15f)
    else -> 1.0f
  }

  val offsetX = when (overlay.animationType) {
    TextAnimationType.SLIDE_RIGHT -> canvasWidth * (overlay.positionX - 0.2f * (1f - progress.coerceIn(0f, 1f)))
    else -> canvasWidth * overlay.positionX
  }

  val displayString = when (overlay.animationType) {
    TextAnimationType.TYPEWRITER -> {
      val charCount = (overlay.text.length * (progress * 2f).coerceIn(0f, 1f)).toInt()
      overlay.text.take(charCount)
    }
    else -> overlay.text
  }

  val targetY = canvasHeight * overlay.positionY

  val textPaint = android.graphics.Paint().apply {
    color = baseColor.copy(alpha = alpha).toArgb()
    textSize = overlay.fontSize * (canvasWidth / 420f).coerceAtLeast(1f)
    textAlign = android.graphics.Paint.Align.CENTER
    isAntiAlias = true
    isFakeBoldText = true
  }

  // Draw background box if enabled
  if (overlay.hasBackground) {
    val textWidth = textPaint.measureText(displayString)
    val textHeight = textPaint.textSize
    val pad = 16f
    drawRoundRect(
      color = Color.Black.copy(alpha = 0.65f),
      topLeft = Offset(offsetX - textWidth / 2f - pad, targetY - textHeight - pad / 2f),
      size = Size(textWidth + pad * 2f, textHeight + pad * 2f),
      cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
    )
  }

  // Draw stroke if enabled
  if (overlay.hasStroke) {
    val strokePaint = android.graphics.Paint(textPaint).apply {
      style = android.graphics.Paint.Style.STROKE
      strokeWidth = 6f
      color = android.graphics.Color.BLACK
    }
    drawContext.canvas.nativeCanvas.drawText(displayString, offsetX, targetY, strokePaint)
  }

  drawContext.canvas.nativeCanvas.drawText(displayString, offsetX, targetY, textPaint)
}

private fun DrawScope.drawSubtitle(
  subtitle: SubtitleSegment,
  progress: Float,
  canvasWidth: Float,
  canvasHeight: Float
) {
  val subY = canvasHeight * 0.84f
  val subX = canvasWidth * 0.5f

  val subPaint = android.graphics.Paint().apply {
    textSize = 34f * (canvasWidth / 420f).coerceAtLeast(1f)
    textAlign = android.graphics.Paint.Align.CENTER
    isAntiAlias = true
    isFakeBoldText = true
  }

  when (subtitle.style) {
    SubtitleStyle.NEON_GLOW -> {
      subPaint.color = android.graphics.Color.WHITE
      subPaint.setShadowLayer(16f, 0f, 0f, android.graphics.Color.parseColor("#06B6D4"))
    }
    SubtitleStyle.KARAOKE -> {
      subPaint.color = android.graphics.Color.parseColor("#FBBF24")
      subPaint.setShadowLayer(8f, 2f, 2f, android.graphics.Color.BLACK)
    }
    SubtitleStyle.YELLOW_BOX -> {
      val textWidth = subPaint.measureText(subtitle.text)
      val textHeight = subPaint.textSize
      val pad = 14f
      drawRoundRect(
        color = Color(0xFFFBBF24),
        topLeft = Offset(subX - textWidth / 2f - pad, subY - textHeight - pad / 2f),
        size = Size(textWidth + pad * 2f, textHeight + pad * 2f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
      )
      subPaint.color = android.graphics.Color.BLACK
    }
    else -> {
      subPaint.color = android.graphics.Color.WHITE
      subPaint.setShadowLayer(6f, 2f, 2f, android.graphics.Color.BLACK)
    }
  }

  drawContext.canvas.nativeCanvas.drawText(subtitle.text, subX, subY, subPaint)
}
