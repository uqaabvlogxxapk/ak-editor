package com.example.ui.editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AutoEnhanceResult
import com.example.ai.GeminiAiService
import com.example.model.ColorFilterType
import com.example.model.MotionTrackingData
import com.example.model.SubtitleSegment
import com.example.model.SubtitleStyle
import com.example.model.TimelineClip
import com.example.model.TrackingKeyframe
import com.example.model.VideoProject
import com.example.ui.theme.AkBgSurface
import com.example.ui.theme.AkBgSurfaceElevated
import com.example.ui.theme.AkBgSurfaceVariant
import com.example.ui.theme.AkCyanAccent
import com.example.ui.theme.AkPurpleLight
import com.example.ui.theme.AkPurplePrimary
import com.example.ui.theme.AkTextMuted
import com.example.ui.theme.AkTextPrimary
import com.example.ui.theme.AkTextSecondary
import kotlinx.coroutines.launch

@Composable
fun AiToolsPanel(
  project: VideoProject,
  selectedClip: TimelineClip?,
  aiService: GeminiAiService,
  onUpdateSubtitles: (List<SubtitleSegment>) -> Unit,
  onUpdateClip: (TimelineClip) -> Unit,
  onUpdateTracking: (MotionTrackingData) -> Unit,
  onToggleSplitPreview: () -> Unit,
  showSplitPreview: Boolean,
  modifier: Modifier = Modifier
) {
  val scope = rememberCoroutineScope()
  var activeSubTab by remember { mutableStateOf("captions") } // captions, enhance, tracking, bg_remove
  var isAiProcessing by remember { mutableStateOf(false) }
  var aiStatusMessage by remember { mutableStateOf<String?>(null) }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(AkBgSurface)
      .padding(12.dp)
      .verticalScroll(rememberScrollState())
  ) {
    // Tool Sub-tabs
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(AkBgSurfaceVariant)
        .padding(4.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      AiSubTabButton(
        label = "Auto Captions",
        icon = Icons.Default.Subtitles,
        isSelected = activeSubTab == "captions",
        onClick = { activeSubTab = "captions" }
      )
      AiSubTabButton(
        label = "Auto Enhance",
        icon = Icons.Default.AutoAwesome,
        isSelected = activeSubTab == "enhance",
        onClick = { activeSubTab = "enhance" }
      )
      AiSubTabButton(
        label = "Motion Track",
        icon = Icons.Default.TrackChanges,
        isSelected = activeSubTab == "tracking",
        onClick = { activeSubTab = "tracking" }
      )
      AiSubTabButton(
        label = "BG Remove",
        icon = Icons.Default.ContentCut,
        isSelected = activeSubTab == "bg_remove",
        onClick = { activeSubTab = "bg_remove" }
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Progress or Status Message
    AnimatedVisibility(visible = isAiProcessing) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(AkBgSurfaceElevated)
          .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        CircularProgressIndicator(
          modifier = Modifier.size(20.dp),
          color = AkCyanAccent,
          strokeWidth = 2.dp
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
          text = aiStatusMessage ?: "AI is processing...",
          color = AkTextPrimary,
          fontSize = 12.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // 1. AUTO CAPTIONS
    if (activeSubTab == "captions") {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("AI Speech-to-Text Captions", color = AkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          Button(
            onClick = {
              scope.launch {
                isAiProcessing = true
                aiStatusMessage = "Transcribing speech and generating timestamped captions..."
                val context = project.clips.firstOrNull()?.name ?: "Video storytelling"
                val captions = aiService.generateAutoCaptions(context, project.durationMs)
                onUpdateSubtitles(captions)
                isAiProcessing = false
                aiStatusMessage = null
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = AkPurplePrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("generate_captions_button")
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Generate Captions", fontSize = 12.sp)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          "Select Caption Animation Style:",
          color = AkTextSecondary,
          fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(SubtitleStyle.entries.toTypedArray()) { style ->
            val isSelected = project.subtitles.firstOrNull()?.style == style
            Surface(
              onClick = {
                val updated = project.subtitles.map { it.copy(style = style) }
                onUpdateSubtitles(updated)
              },
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) AkPurplePrimary else AkBgSurfaceElevated,
              modifier = Modifier.border(
                1.dp,
                if (isSelected) AkCyanAccent else Color.Transparent,
                RoundedCornerShape(8.dp)
              )
            ) {
              Text(
                text = style.displayName,
                color = AkTextPrimary,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Existing Subtitles List
        if (project.subtitles.isNotEmpty()) {
          Text("Generated Segments (${project.subtitles.size}):", color = AkTextSecondary, fontSize = 11.sp)
          Spacer(modifier = Modifier.height(6.dp))
          project.subtitles.take(5).forEachIndexed { idx, sub ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(AkBgSurfaceElevated)
                .padding(8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${idx + 1}. \"${sub.text}\"",
                color = AkTextPrimary,
                fontSize = 12.sp,
                maxLines = 1,
                modifier = Modifier.weight(1f)
              )
              Text(
                text = "${sub.startTimeMs / 1000}s - ${sub.endTimeMs / 1000}s",
                color = AkCyanAccent,
                fontSize = 10.sp
              )
            }
          }
        }
      }
    }

    // 2. AUTO ENHANCE
    if (activeSubTab == "enhance") {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("AI Auto Enhance", color = AkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("One-click optimal color, contrast & tone curve", color = AkTextSecondary, fontSize = 11.sp)
          }

          Button(
            onClick = {
              if (selectedClip != null) {
                scope.launch {
                  isAiProcessing = true
                  aiStatusMessage = "Analyzing frame dynamic range and tone balance..."
                  val res = aiService.analyzeAutoEnhance(selectedClip.name, selectedClip.colorFilter)
                  val updated = selectedClip.copy(
                    brightness = res.brightness,
                    contrast = res.contrast,
                    saturation = res.saturation,
                    temperature = res.temperature,
                    vignette = res.vignette,
                    colorFilter = res.suggestedFilter
                  )
                  onUpdateClip(updated)
                  isAiProcessing = false
                  aiStatusMessage = null
                }
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = AkCyanAccent),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("apply_auto_enhance_button")
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Enhance Clip", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Before & After Split Comparison Toggle
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AkBgSurfaceElevated)
            .clickable { onToggleSplitPreview() }
            .padding(10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Compare, contentDescription = null, tint = AkCyanAccent, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Before / After Split Screen View", color = AkTextPrimary, fontSize = 12.sp)
          }
          Switch(
            checked = showSplitPreview,
            onCheckedChange = { onToggleSplitPreview() },
            colors = SwitchDefaults.colors(
              checkedThumbColor = AkCyanAccent,
              checkedTrackColor = AkPurplePrimary
            )
          )
        }

        if (selectedClip != null) {
          Spacer(modifier = Modifier.height(10.dp))
          Text("Fine-Tune AI Enhancement:", color = AkTextSecondary, fontSize = 11.sp)

          // Contrast Slider
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Contrast", color = AkTextPrimary, fontSize = 12.sp)
            Text("${String.format("%.2f", selectedClip.contrast)}x", color = AkCyanAccent, fontSize = 12.sp)
          }
          Slider(
            value = selectedClip.contrast,
            onValueChange = { onUpdateClip(selectedClip.copy(contrast = it)) },
            valueRange = 0.8f..1.8f,
            colors = SliderDefaults.colors(thumbColor = AkCyanAccent, activeTrackColor = AkPurplePrimary)
          )

          // Saturation Slider
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Saturation", color = AkTextPrimary, fontSize = 12.sp)
            Text("${String.format("%.2f", selectedClip.saturation)}x", color = AkCyanAccent, fontSize = 12.sp)
          }
          Slider(
            value = selectedClip.saturation,
            onValueChange = { onUpdateClip(selectedClip.copy(saturation = it)) },
            valueRange = 0.5f..1.8f,
            colors = SliderDefaults.colors(thumbColor = AkCyanAccent, activeTrackColor = AkPurplePrimary)
          )
        }
      }
    }

    // 3. MOTION TRACKING
    if (activeSubTab == "tracking") {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("AI Camera & Object Tracking", color = AkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Track focal subject and pin text/stickers", color = AkTextSecondary, fontSize = 11.sp)
          }
          Switch(
            checked = project.motionTracking.isEnabled,
            onCheckedChange = { isEnabled ->
              onUpdateTracking(
                project.motionTracking.copy(
                  isEnabled = isEnabled,
                  keyframes = if (isEnabled && project.motionTracking.keyframes.isEmpty()) {
                    listOf(
                      TrackingKeyframe(0L, 0.4f, 0.4f),
                      TrackingKeyframe(project.durationMs / 2, 0.55f, 0.45f),
                      TrackingKeyframe(project.durationMs, 0.6f, 0.4f)
                    )
                  } else project.motionTracking.keyframes
                )
              )
            },
            colors = SwitchDefaults.colors(checkedThumbColor = AkCyanAccent, checkedTrackColor = AkPurplePrimary)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (project.motionTracking.isEnabled) {
          Text(
            "Tracking Active on: ${project.motionTracking.targetLabel}",
            color = AkCyanAccent,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            "Tracked reticle computes path over time. Any titles with 'Track to Motion' enabled will follow this path automatically.",
            color = AkTextSecondary,
            fontSize = 11.sp
          )
        }
      }
    }

    // 4. BACKGROUND REMOVAL
    if (activeSubTab == "bg_remove") {
      Column {
        Text("AI Subject Segmentation & BG Removal", color = AkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text("Isolate foreground actor with neural alpha mask", color = AkTextSecondary, fontSize = 11.sp)

        Spacer(modifier = Modifier.height(10.dp))

        if (selectedClip != null) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(AkBgSurfaceElevated)
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Enable Alpha Mask Cutout", color = AkTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
              Text("Removes background and reveals layers underneath", color = AkTextSecondary, fontSize = 11.sp)
            }
            Switch(
              checked = selectedClip.chromaKeyEnabled,
              onCheckedChange = { isEnabled ->
                onUpdateClip(selectedClip.copy(chromaKeyEnabled = isEnabled))
              },
              colors = SwitchDefaults.colors(checkedThumbColor = AkCyanAccent, checkedTrackColor = AkPurplePrimary)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text("Backdrop Replacement:", color = AkTextSecondary, fontSize = 11.sp)
          Spacer(modifier = Modifier.height(6.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Transparent", "Studio Dark", "Neon City", "Mountain").forEach { plate ->
              Surface(
                onClick = {},
                shape = RoundedCornerShape(6.dp),
                color = AkBgSurfaceElevated,
                modifier = Modifier.border(1.dp, Color(0xFF262145), RoundedCornerShape(6.dp))
              ) {
                Text(plate, color = AkTextPrimary, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp))
              }
            }
          }
        } else {
          Text("Select a clip on the timeline to configure background removal.", color = AkTextMuted, fontSize = 12.sp)
        }
      }
    }
  }
}

@Composable
private fun AiSubTabButton(
  label: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Surface(
    onClick = onClick,
    shape = RoundedCornerShape(6.dp),
    color = if (isSelected) AkPurplePrimary else Color.Transparent,
    modifier = Modifier.padding(2.dp)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isSelected) AkTextPrimary else AkTextMuted,
        modifier = Modifier.size(14.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = label,
        color = if (isSelected) AkTextPrimary else AkTextMuted,
        fontSize = 11.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
      )
    }
  }
}
