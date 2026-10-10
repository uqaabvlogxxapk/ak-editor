package com.example.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.SampleMediaLibrary
import com.example.model.ClipType
import com.example.model.TimelineClip
import com.example.model.VideoProject
import com.example.ui.theme.AkBgSurface
import com.example.ui.theme.AkBgSurfaceElevated
import com.example.ui.theme.AkCyanAccent
import com.example.ui.theme.AkError
import com.example.ui.theme.AkPurplePrimary
import com.example.ui.theme.AkTextMuted
import com.example.ui.theme.AkTextPrimary
import com.example.ui.theme.AkTextSecondary

@Composable
fun AudioStudioPanel(
  project: VideoProject,
  selectedClip: TimelineClip?,
  onUpdateClip: (TimelineClip) -> Unit,
  onAddAudioTrack: (TimelineClip) -> Unit,
  modifier: Modifier = Modifier
) {
  var isRecordingVoiceOver by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(AkBgSurface)
      .padding(12.dp)
      .verticalScroll(rememberScrollState())
  ) {
    Text("Audio Studio & Sound Mixing", color = AkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    Text("Control volume, ducking, fades, and record voice-overs", color = AkTextSecondary, fontSize = 11.sp)

    Spacer(modifier = Modifier.height(12.dp))

    // Voice-Over Recorder Action
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
        Text("Voice-Over Recording", color = AkTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Text(
          if (isRecordingVoiceOver) "Recording live microphone audio..." else "Tap button to record commentary",
          color = if (isRecordingVoiceOver) AkError else AkTextSecondary,
          fontSize = 11.sp
        )
      }

      Button(
        onClick = {
          if (!isRecordingVoiceOver) {
            isRecordingVoiceOver = true
          } else {
            isRecordingVoiceOver = false
            // Create a voice-over clip on track 2
            val voiceClip = TimelineClip(
              name = "Voice-over Take 1",
              type = ClipType.AUDIO,
              trackIndex = 2,
              startTimeMs = 0L,
              durationMs = 8000L,
              volume = 1.2f,
              fadeInMs = 300L,
              fadeOutMs = 500L
            )
            onAddAudioTrack(voiceClip)
          }
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isRecordingVoiceOver) AkError else AkPurplePrimary
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.testTag("voice_over_button")
      ) {
        Icon(
          if (isRecordingVoiceOver) Icons.Default.MicOff else Icons.Default.Mic,
          contentDescription = null,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(if (isRecordingVoiceOver) "Stop Rec" else "Record Mic", fontSize = 12.sp)
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Clip Specific Audio Controls
    if (selectedClip != null) {
      Text("Selected Clip Audio (${selectedClip.name}):", color = AkTextSecondary, fontSize = 11.sp)
      Spacer(modifier = Modifier.height(6.dp))

      // Volume / Gain Slider
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = AkCyanAccent, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Volume / Gain", color = AkTextPrimary, fontSize = 12.sp)
        }
        Text("${(selectedClip.volume * 100).toInt()}%", color = AkCyanAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
      Slider(
        value = selectedClip.volume,
        onValueChange = { onUpdateClip(selectedClip.copy(volume = it)) },
        valueRange = 0.0f..2.0f,
        colors = SliderDefaults.colors(thumbColor = AkCyanAccent, activeTrackColor = AkPurplePrimary)
      )

      // Fade In Slider
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Fade-In Duration", color = AkTextPrimary, fontSize = 12.sp)
        Text("${selectedClip.fadeInMs / 1000f}s", color = AkCyanAccent, fontSize = 12.sp)
      }
      Slider(
        value = selectedClip.fadeInMs.toFloat(),
        onValueChange = { onUpdateClip(selectedClip.copy(fadeInMs = it.toLong())) },
        valueRange = 0f..4000f,
        colors = SliderDefaults.colors(thumbColor = AkCyanAccent, activeTrackColor = AkPurplePrimary)
      )

      // Fade Out Slider
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Fade-Out Duration", color = AkTextPrimary, fontSize = 12.sp)
        Text("${selectedClip.fadeOutMs / 1000f}s", color = AkCyanAccent, fontSize = 12.sp)
      }
      Slider(
        value = selectedClip.fadeOutMs.toFloat(),
        onValueChange = { onUpdateClip(selectedClip.copy(fadeOutMs = it.toLong())) },
        valueRange = 0f..4000f,
        colors = SliderDefaults.colors(thumbColor = AkCyanAccent, activeTrackColor = AkPurplePrimary)
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Stock Music & Sound Tracks Library
    Text("Add Stock Music & Soundtracks:", color = AkTextSecondary, fontSize = 11.sp)
    Spacer(modifier = Modifier.height(6.dp))

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      items(SampleMediaLibrary.audioItems) { audio ->
        Surface(
          onClick = {
            val audioClip = TimelineClip(
              name = audio.title,
              type = ClipType.AUDIO,
              trackIndex = 2,
              startTimeMs = 0L,
              durationMs = audio.durationMs.coerceAtMost(project.durationMs),
              volume = 0.9f,
              sampleAssetId = audio.id
            )
            onAddAudioTrack(audioClip)
          },
          shape = RoundedCornerShape(8.dp),
          color = AkBgSurfaceElevated,
          modifier = Modifier.border(1.dp, Color(0xFF262145), RoundedCornerShape(8.dp))
        ) {
          Column(modifier = Modifier.padding(10.dp).width(140.dp)) {
            Text(audio.title, color = AkTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Spacer(modifier = Modifier.height(2.dp))
            Text("${audio.durationMs / 1000}s", color = AkCyanAccent, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Add, contentDescription = null, tint = AkPurplePrimary, modifier = Modifier.size(14.dp))
              Text("Add to timeline", color = AkPurplePrimary, fontSize = 10.sp)
            }
          }
        }
      }
    }
  }
}
