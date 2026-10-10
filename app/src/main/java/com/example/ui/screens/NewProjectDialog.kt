package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AspectRatio
import com.example.model.ClipType
import com.example.model.ExportFps
import com.example.model.ExportResolution
import com.example.model.TimelineClip
import com.example.model.VideoProject
import com.example.ui.theme.AkBgSurface
import com.example.ui.theme.AkBgSurfaceElevated
import com.example.ui.theme.AkCyanAccent
import com.example.ui.theme.AkPurplePrimary
import com.example.ui.theme.AkTextPrimary
import com.example.ui.theme.AkTextSecondary
import java.util.UUID

@Composable
fun NewProjectDialog(
  onDismiss: () -> Unit,
  onCreateProject: (VideoProject) -> Unit
) {
  var title by remember { mutableStateOf("My Video Project") }
  var selectedRatio by remember { mutableStateOf(AspectRatio.RATIO_16_9) }
  var selectedResolution by remember { mutableStateOf(ExportResolution.RES_1080P) }
  var selectedFps by remember { mutableStateOf(ExportFps.FPS_30) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = AkBgSurface,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .border(1.dp, Color(0xFF262145), RoundedCornerShape(16.dp))
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Create New Project", color = AkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = AkTextSecondary)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Project Title") },
          modifier = Modifier.fillMaxWidth().testTag("new_project_title_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = AkTextPrimary,
            unfocusedTextColor = AkTextPrimary,
            focusedBorderColor = AkCyanAccent,
            unfocusedBorderColor = Color(0xFF332D59)
          )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text("Aspect Ratio:", color = AkTextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(6.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          AspectRatio.entries.forEach { ratio ->
            val isSelected = selectedRatio == ratio
            Surface(
              onClick = { selectedRatio = ratio },
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) AkPurplePrimary else AkBgSurfaceElevated,
              modifier = Modifier
                .weight(1f)
                .border(1.dp, if (isSelected) AkCyanAccent else Color.Transparent, RoundedCornerShape(8.dp))
            ) {
              Column(
                modifier = Modifier.padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(ratio.label, color = AkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(
                  text = when (ratio) {
                    AspectRatio.RATIO_16_9 -> "YouTube"
                    AspectRatio.RATIO_9_16 -> "Reels/TikTok"
                    AspectRatio.RATIO_1_1 -> "Square"
                    AspectRatio.RATIO_4_5 -> "Feed"
                  },
                  color = if (isSelected) AkTextPrimary else AkTextSecondary,
                  fontSize = 9.sp
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text("Initial Resolution:", color = AkTextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(6.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          listOf(ExportResolution.RES_720P, ExportResolution.RES_1080P, ExportResolution.RES_4K).forEach { res ->
            val isSelected = selectedResolution == res
            Surface(
              onClick = { selectedResolution = res },
              shape = RoundedCornerShape(6.dp),
              color = if (isSelected) AkPurplePrimary else AkBgSurfaceElevated,
              modifier = Modifier
                .weight(1f)
                .border(1.dp, if (isSelected) AkCyanAccent else Color.Transparent, RoundedCornerShape(6.dp))
            ) {
              Text(
                text = res.label.take(5),
                color = AkTextPrimary,
                fontSize = 11.sp,
                modifier = Modifier.padding(vertical = 6.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = {
            val starterProject = VideoProject(
              id = UUID.randomUUID().toString(),
              title = title.ifBlank { "Untitled Project" },
              aspectRatio = selectedRatio,
              resolution = selectedResolution,
              fps = selectedFps,
              durationMs = 12000L,
              clips = listOf(
                TimelineClip(
                  name = "Opening Scene",
                  type = ClipType.VIDEO,
                  trackIndex = 0,
                  startTimeMs = 0L,
                  durationMs = 6000L,
                  sampleAssetId = "cyberpunk_city"
                ),
                TimelineClip(
                  name = "Second Shot",
                  type = ClipType.VIDEO,
                  trackIndex = 0,
                  startTimeMs = 6000L,
                  durationMs = 6000L,
                  sampleAssetId = "mountain_drone"
                )
              )
            )
            onCreateProject(starterProject)
          },
          colors = ButtonDefaults.buttonColors(containerColor = AkCyanAccent),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth().height(46.dp).testTag("confirm_create_project_button")
        ) {
          Text("Launch Editor Workspace", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
      }
    }
  }
}
