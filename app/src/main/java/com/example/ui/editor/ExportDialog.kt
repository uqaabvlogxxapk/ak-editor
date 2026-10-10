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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.ExportFps
import com.example.model.ExportResolution
import com.example.model.VideoProject
import com.example.ui.theme.AkBgDark
import com.example.ui.theme.AkBgSurface
import com.example.ui.theme.AkBgSurfaceElevated
import com.example.ui.theme.AkCyanAccent
import com.example.ui.theme.AkPurplePrimary
import com.example.ui.theme.AkSuccess
import com.example.ui.theme.AkTextMuted
import com.example.ui.theme.AkTextPrimary
import com.example.ui.theme.AkTextSecondary
import kotlinx.coroutines.delay

@Composable
fun ExportDialog(
  project: VideoProject,
  isProUser: Boolean,
  onDismiss: () -> Unit,
  onOpenUpgradeDialog: () -> Unit
) {
  var selectedResolution by remember { mutableStateOf(project.resolution) }
  var selectedFps by remember { mutableStateOf(project.fps) }
  var removeWatermark by remember { mutableStateOf(!project.watermarkEnabled) }
  var watermarkText by remember { mutableStateOf(project.customWatermarkText) }

  var isRendering by remember { mutableStateOf(false) }
  var renderProgress by remember { mutableFloatStateOf(0f) }
  var renderedFrames by remember { mutableIntStateOf(0) }
  var isRenderComplete by remember { mutableStateOf(false) }

  val totalFrames = ((project.durationMs / 1000f) * selectedFps.fps).toInt().coerceAtLeast(30)

  LaunchedEffect(isRendering) {
    if (isRendering) {
      renderProgress = 0f
      renderedFrames = 0
      while (renderedFrames < totalFrames) {
        delay(40)
        renderedFrames += (totalFrames / 35).coerceAtLeast(1)
        renderProgress = (renderedFrames.toFloat() / totalFrames).coerceIn(0f, 1f)
      }
      isRendering = false
      isRenderComplete = true
    }
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = AkBgSurface,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .border(1.dp, Color(0xFF262145), RoundedCornerShape(16.dp))
    ) {
      Column(
        modifier = Modifier
          .padding(18.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = if (isRenderComplete) "Export Complete!" else "Export Studio",
              color = AkTextPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp
            )
            Text(
              text = "${selectedResolution.label} • ${selectedFps.label}",
              color = AkCyanAccent,
              fontSize = 12.sp
            )
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = AkTextSecondary)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (isRenderComplete) {
          // Render Success Screen
          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              Icons.Default.CheckCircle,
              contentDescription = null,
              tint = AkSuccess,
              modifier = Modifier.size(54.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text("Video Successfully Rendered", color = AkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(
              "${selectedResolution.width}x${selectedResolution.height} MP4 • H.264 • No Watermark",
              color = AkTextSecondary,
              fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
              onClick = onDismiss,
              colors = ButtonDefaults.buttonColors(containerColor = AkCyanAccent),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth().testTag("save_rendered_video_button")
            ) {
              Icon(Icons.Default.Download, contentDescription = null, tint = Color.Black)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Save to Gallery", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
              onClick = onDismiss,
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(Icons.Default.Share, contentDescription = null, tint = AkTextPrimary)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Share Video", color = AkTextPrimary)
            }
          }
        } else if (isRendering) {
          // Rendering Progress Bar
          Column(modifier = Modifier.fillMaxWidth()) {
            Text("Encoding timeline layers...", color = AkTextPrimary, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
              progress = { renderProgress },
              modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
              color = AkCyanAccent,
              trackColor = Color(0xFF262145)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Frame $renderedFrames / $totalFrames", color = AkTextSecondary, fontSize = 11.sp)
              Text("${(renderProgress * 100).toInt()}%", color = AkCyanAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        } else {
          // Export Settings Controls
          Text("Target Resolution:", color = AkTextSecondary, fontSize = 11.sp)
          Spacer(modifier = Modifier.height(6.dp))

          ExportResolution.entries.forEach { res ->
            val isSelected = selectedResolution == res
            val locked = res.isPro && !isProUser

            Surface(
              onClick = {
                if (locked) {
                  onOpenUpgradeDialog()
                } else {
                  selectedResolution = res
                }
              },
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) AkPurplePrimary else AkBgSurfaceElevated,
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
                .border(1.dp, if (isSelected) AkCyanAccent else Color.Transparent, RoundedCornerShape(8.dp))
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(res.label, color = AkTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                  Text("${res.width} x ${res.height}", color = AkTextSecondary, fontSize = 10.sp)
                }

                if (res.isPro) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    if (locked) {
                      Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                      "PRO",
                      color = Color(0xFFF59E0B),
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier
                        .background(Color(0xFF78350F), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Frame Rate
          Text("Frame Rate:", color = AkTextSecondary, fontSize = 11.sp)
          Spacer(modifier = Modifier.height(6.dp))

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ExportFps.entries.forEach { fps ->
              val isSelected = selectedFps == fps
              Surface(
                onClick = { selectedFps = fps },
                shape = RoundedCornerShape(6.dp),
                color = if (isSelected) AkPurplePrimary else AkBgSurfaceElevated,
                modifier = Modifier
                  .weight(1f)
                  .border(1.dp, if (isSelected) AkCyanAccent else Color.Transparent, RoundedCornerShape(6.dp))
              ) {
                Text(
                  text = "${fps.fps} FPS",
                  color = AkTextPrimary,
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  modifier = Modifier.padding(vertical = 8.dp),
                  textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Clean Watermark Option
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(AkBgSurfaceElevated)
              .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("No Watermark Export", color = AkTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              Text("Clean output without application logos", color = AkSuccess, fontSize = 10.sp)
            }
            Switch(
              checked = removeWatermark,
              onCheckedChange = { removeWatermark = it },
              colors = SwitchDefaults.colors(checkedThumbColor = AkSuccess, checkedTrackColor = Color(0xFF065F46))
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Start Render Button
          Button(
            onClick = { isRendering = true },
            colors = ButtonDefaults.buttonColors(containerColor = AkPurplePrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().height(46.dp).testTag("start_render_button")
          ) {
            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Render & Export Video", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }
        }
      }
    }
  }
}
