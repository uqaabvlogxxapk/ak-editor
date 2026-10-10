package com.example.ui.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ClipType
import com.example.model.TimelineClip
import com.example.model.VideoProject
import com.example.ui.theme.AkBgDark
import com.example.ui.theme.AkBgSurface
import com.example.ui.theme.AkCyanAccent
import com.example.ui.theme.AkPlayhead
import com.example.ui.theme.AkPurpleLight
import com.example.ui.theme.AkPurplePrimary
import com.example.ui.theme.AkTextMuted
import com.example.ui.theme.AkTextPrimary
import com.example.ui.theme.AkTextSecondary
import com.example.ui.theme.AkTimelineBg
import com.example.ui.theme.AkTrackBg
import com.example.ui.theme.AkTrimHandle
import java.util.Locale

@Composable
fun TimelineView(
  project: VideoProject,
  currentPlayheadMs: Long,
  isPlaying: Boolean,
  selectedClipId: String?,
  onPlayheadChanged: (Long) -> Unit,
  onTogglePlay: () -> Unit,
  onSelectClip: (String?) -> Unit,
  onSplitClip: () -> Unit,
  onDeleteClip: (String) -> Unit,
  onDuplicateClip: (String) -> Unit,
  onTrimClip: (clipId: String, newStartMs: Long, newDurationMs: Long) -> Unit,
  modifier: Modifier = Modifier
) {
  var zoomScale by remember { mutableFloatStateOf(1.0f) }
  val scrollState = rememberScrollState()

  // Base pixels per millisecond (e.g., 0.08 px/ms at 1.0x zoom => 80px per second)
  val pxPerMs = 0.08f * zoomScale
  val totalTimelineWidthDp = ((project.durationMs * pxPerMs) + 300).dp

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(AkTimelineBg)
      .padding(top = 4.dp)
  ) {
    // Timeline Toolbar (Transport Controls, Timecode, Split, Delete, Duplicate, Zoom)
    TimelineToolbar(
      project = project,
      currentPlayheadMs = currentPlayheadMs,
      isPlaying = isPlaying,
      selectedClipId = selectedClipId,
      zoomScale = zoomScale,
      onTogglePlay = onTogglePlay,
      onSeekRelative = { offsetMs ->
        val newPos = (currentPlayheadMs + offsetMs).coerceIn(0L, project.durationMs)
        onPlayheadChanged(newPos)
      },
      onSplitClip = onSplitClip,
      onDeleteClip = {
        selectedClipId?.let { onDeleteClip(it) }
      },
      onDuplicateClip = {
        selectedClipId?.let { onDuplicateClip(it) }
      },
      onZoomChanged = { zoomScale = it }
    )

    // Tracks Container
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
        .horizontalScroll(scrollState)
    ) {
      Box(modifier = Modifier.width(totalTimelineWidthDp).fillMaxHeight()) {
        // Time Ruler Background
        TimeRuler(
          durationMs = project.durationMs,
          pxPerMs = pxPerMs,
          modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
        )

        // Tracks Column (V1 Video, V2 Overlay, A1 Audio, T1 Text)
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 26.dp)
        ) {
          // Track V1 (Main Video)
          TrackLane(
            title = "V1",
            icon = Icons.Default.Movie,
            color = Color(0xFF6D28D9),
            heightDp = 44
          ) {
            val v1Clips = project.clips.filter { it.trackIndex == 0 }
            v1Clips.forEach { clip ->
              ClipBlock(
                clip = clip,
                pxPerMs = pxPerMs,
                isSelected = clip.id == selectedClipId,
                onSelect = { onSelectClip(clip.id) },
                onTrim = { start, dur -> onTrimClip(clip.id, start, dur) }
              )
            }
          }

          Spacer(modifier = Modifier.height(2.dp))

          // Track V2 (Overlay / PIP)
          TrackLane(
            title = "V2",
            icon = Icons.Default.Movie,
            color = Color(0xFF4338CA),
            heightDp = 36
          ) {
            val v2Clips = project.clips.filter { it.trackIndex == 1 }
            v2Clips.forEach { clip ->
              ClipBlock(
                clip = clip,
                pxPerMs = pxPerMs,
                isSelected = clip.id == selectedClipId,
                onSelect = { onSelectClip(clip.id) },
                onTrim = { start, dur -> onTrimClip(clip.id, start, dur) }
              )
            }
          }

          Spacer(modifier = Modifier.height(2.dp))

          // Track A1 (Audio)
          TrackLane(
            title = "A1",
            icon = Icons.Default.Audiotrack,
            color = Color(0xFF0D9488),
            heightDp = 36
          ) {
            val aClips = project.clips.filter { it.trackIndex == 2 || it.type == ClipType.AUDIO }
            aClips.forEach { clip ->
              ClipBlock(
                clip = clip,
                pxPerMs = pxPerMs,
                isSelected = clip.id == selectedClipId,
                isAudio = true,
                onSelect = { onSelectClip(clip.id) },
                onTrim = { start, dur -> onTrimClip(clip.id, start, dur) }
              )
            }
          }

          Spacer(modifier = Modifier.height(2.dp))

          // Track T1 (Text & Captions)
          TrackLane(
            title = "T1",
            icon = Icons.Default.Title,
            color = Color(0xFFD97706),
            heightDp = 28
          ) {
            project.textOverlays.forEach { textOverlay ->
              val leftDp = (textOverlay.startTimeMs * pxPerMs).dp
              val widthDp = (textOverlay.durationMs * pxPerMs).coerceAtLeast(40f).dp
              Box(
                modifier = Modifier
                  .offset(x = leftDp)
                  .width(widthDp)
                  .height(24.dp)
                  .clip(RoundedCornerShape(4.dp))
                  .background(Color(0xFFB45309).copy(alpha = 0.85f))
                  .padding(horizontal = 6.dp),
                contentAlignment = Alignment.CenterStart
              ) {
                Text(
                  text = textOverlay.text,
                  color = Color.White,
                  fontSize = 10.sp,
                  maxLines = 1
                )
              }
            }
          }
        }

        // Playhead Scrub Needle
        val playheadOffsetDp = (currentPlayheadMs * pxPerMs).dp
        PlayheadNeedle(
          offsetDp = playheadOffsetDp,
          onDrag = { deltaPx ->
            val deltaMs = (deltaPx / pxPerMs).toLong()
            val newPos = (currentPlayheadMs + deltaMs).coerceIn(0L, project.durationMs)
            onPlayheadChanged(newPos)
          }
        )
      }
    }
  }
}

@Composable
private fun TimelineToolbar(
  project: VideoProject,
  currentPlayheadMs: Long,
  isPlaying: Boolean,
  selectedClipId: String?,
  zoomScale: Float,
  onTogglePlay: () -> Unit,
  onSeekRelative: (Long) -> Unit,
  onSplitClip: () -> Unit,
  onDeleteClip: () -> Unit,
  onDuplicateClip: () -> Unit,
  onZoomChanged: (Float) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    // Playback & Timecode
    Row(verticalAlignment = Alignment.CenterVertically) {
      IconButton(
        onClick = { onSeekRelative(-1000L) },
        modifier = Modifier.size(36.dp)
      ) {
        Icon(Icons.Default.FastRewind, contentDescription = "Rewind 1s", tint = AkTextSecondary)
      }

      IconButton(
        onClick = onTogglePlay,
        modifier = Modifier
          .size(40.dp)
          .background(AkPurplePrimary, CircleShape)
          .testTag("play_pause_button")
      ) {
        Icon(
          imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
          contentDescription = if (isPlaying) "Pause" else "Play",
          tint = AkTextPrimary
        )
      }

      IconButton(
        onClick = { onSeekRelative(1000L) },
        modifier = Modifier.size(36.dp)
      ) {
        Icon(Icons.Default.FastForward, contentDescription = "Forward 1s", tint = AkTextSecondary)
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Timecode
      Text(
        text = "${formatTimecode(currentPlayheadMs)} / ${formatTimecode(project.durationMs)}",
        color = AkCyanAccent,
        fontSize = 12.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold
      )
    }

    // Clip Actions (Split, Duplicate, Delete)
    Row(verticalAlignment = Alignment.CenterVertically) {
      // Split
      Surface(
        onClick = onSplitClip,
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFF262145),
        modifier = Modifier
          .height(30.dp)
          .testTag("split_clip_button")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Split", color = AkTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
      }

      Spacer(modifier = Modifier.width(6.dp))

      // Duplicate
      if (selectedClipId != null) {
        IconButton(
          onClick = onDuplicateClip,
          modifier = Modifier.size(30.dp)
        ) {
          Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate Clip", tint = AkPurpleLight, modifier = Modifier.size(18.dp))
        }

        IconButton(
          onClick = onDeleteClip,
          modifier = Modifier.size(30.dp)
        ) {
          Icon(Icons.Default.Delete, contentDescription = "Delete Clip", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Zoom Slider
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = AkTextMuted, modifier = Modifier.size(16.dp))
        Slider(
          value = zoomScale,
          onValueChange = onZoomChanged,
          valueRange = 0.5f..3.0f,
          modifier = Modifier.width(70.dp),
          colors = SliderDefaults.colors(
            thumbColor = AkCyanAccent,
            activeTrackColor = AkPurplePrimary,
            inactiveTrackColor = AkTrackBg
          )
        )
        Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = AkTextMuted, modifier = Modifier.size(16.dp))
      }
    }
  }
}

@Composable
private fun TrackLane(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  color: Color,
  heightDp: Int,
  content: @Composable () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(heightDp.dp)
      .background(AkTrackBg)
      .border(0.5.dp, Color(0xFF1E1B38))
  ) {
    // Header Label on left
    Row(
      modifier = Modifier
        .align(Alignment.CenterStart)
        .padding(horizontal = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = title,
        color = color.copy(alpha = 0.7f),
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold
      )
    }

    content()
  }
}

@Composable
private fun ClipBlock(
  clip: TimelineClip,
  pxPerMs: Float,
  isSelected: Boolean,
  isAudio: Boolean = false,
  onSelect: () -> Unit,
  onTrim: (newStartMs: Long, newDurationMs: Long) -> Unit
) {
  val leftDp = (clip.startTimeMs * pxPerMs).dp
  val widthDp = (clip.durationMs * pxPerMs).coerceAtLeast(32f).dp

  val borderColor = if (isSelected) AkTrimHandle else Color.Transparent
  val bgColor = if (isAudio) Color(0xFF0F766E) else Color(0xFF6D28D9)

  Box(
    modifier = Modifier
      .offset(x = leftDp)
      .width(widthDp)
      .fillMaxHeight()
      .clip(RoundedCornerShape(4.dp))
      .background(bgColor)
      .border(if (isSelected) 2.dp else 1.dp, if (isSelected) AkCyanAccent else Color(0xFF4C1D95), RoundedCornerShape(4.dp))
      .clickable { onSelect() }
      .padding(horizontal = 4.dp)
  ) {
    // Audio Waveform or Video Details
    if (isAudio) {
      AudioWaveformVisual(modifier = Modifier.fillMaxSize())
    }

    Row(
      modifier = Modifier
        .align(Alignment.CenterStart)
        .fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = clip.name,
        color = Color.White,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        maxLines = 1
      )
      if (clip.colorFilter != com.example.model.ColorFilterType.NONE) {
        Text(
          text = clip.colorFilter.displayName.take(4),
          color = AkCyanAccent,
          fontSize = 9.sp
        )
      }
    }

    // Trim Handles if selected
    if (isSelected) {
      // Left Handle
      Box(
        modifier = Modifier
          .align(Alignment.CenterStart)
          .width(6.dp)
          .fillMaxHeight()
          .background(AkCyanAccent)
      )
      // Right Handle
      Box(
        modifier = Modifier
          .align(Alignment.CenterEnd)
          .width(6.dp)
          .fillMaxHeight()
          .background(AkCyanAccent)
      )
    }
  }
}

@Composable
private fun AudioWaveformVisual(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier) {
    val count = (size.width / 8f).toInt().coerceAtLeast(4)
    val midY = size.height / 2f
    for (i in 0 until count) {
      val x = i * 8f
      val barH = (size.height * 0.35f * (0.3f + 0.7f * kotlin.math.abs(kotlin.math.sin(i * 0.8f))))
      drawLine(
        color = Color.White.copy(alpha = 0.25f),
        start = Offset(x, midY - barH),
        end = Offset(x, midY + barH),
        strokeWidth = 3f
      )
    }
  }
}

@Composable
private fun TimeRuler(
  durationMs: Long,
  pxPerMs: Float,
  modifier: Modifier = Modifier
) {
  Canvas(modifier = modifier) {
    val totalSec = (durationMs / 1000).toInt() + 2
    for (sec in 0..totalSec) {
      val x = sec * 1000 * pxPerMs
      // Major second mark
      drawLine(
        color = Color(0xFF475569),
        start = Offset(x, size.height),
        end = Offset(x, size.height - 12f),
        strokeWidth = 2f
      )
      // Sub-second marks (half seconds)
      val halfX = (sec * 1000 + 500) * pxPerMs
      drawLine(
        color = Color(0xFF334155),
        start = Offset(halfX, size.height),
        end = Offset(halfX, size.height - 6f),
        strokeWidth = 1f
      )
    }
  }
}

@Composable
private fun PlayheadNeedle(
  offsetDp: androidx.compose.ui.unit.Dp,
  onDrag: (Float) -> Unit
) {
  Box(
    modifier = Modifier
      .offset(x = offsetDp - 6.dp)
      .width(12.dp)
      .fillMaxHeight()
      .pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
          change.consume()
          onDrag(dragAmount.x)
        }
      }
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      // Playhead Top Triangle
      val trianglePath = androidx.compose.ui.graphics.Path().apply {
        moveTo(0f, 0f)
        lineTo(size.width, 0f)
        lineTo(size.width / 2f, 14f)
        close()
      }
      drawPath(trianglePath, color = AkPlayhead)

      // Playhead Line
      drawLine(
        color = AkPlayhead,
        start = Offset(size.width / 2f, 12f),
        end = Offset(size.width / 2f, size.height),
        strokeWidth = 3f
      )
    }
  }
}

fun formatTimecode(ms: Long): String {
  val totalSeconds = ms / 1000
  val minutes = totalSeconds / 60
  val seconds = totalSeconds % 60
  val hundredths = (ms % 1000) / 10
  return String.format(Locale.US, "%02d:%02d.%02d", minutes, seconds, hundredths)
}
