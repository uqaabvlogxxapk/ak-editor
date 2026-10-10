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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TimelineClip
import com.example.model.TransitionType
import com.example.ui.theme.AkBgSurface
import com.example.ui.theme.AkBgSurfaceElevated
import com.example.ui.theme.AkCyanAccent
import com.example.ui.theme.AkPurplePrimary
import com.example.ui.theme.AkTextMuted
import com.example.ui.theme.AkTextPrimary
import com.example.ui.theme.AkTextSecondary

@Composable
fun TransitionsPanel(
  selectedClip: TimelineClip?,
  onUpdateClip: (TimelineClip) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(AkBgSurface)
      .padding(12.dp)
  ) {
    Text("Video Transitions", color = AkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    Text("Select entrance transition for the active clip", color = AkTextSecondary, fontSize = 11.sp)

    Spacer(modifier = Modifier.height(10.dp))

    if (selectedClip == null) {
      Text("Select a video clip on the timeline to apply transitions.", color = AkTextMuted, fontSize = 13.sp)
      return@Column
    }

    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.height(130.dp)
    ) {
      items(TransitionType.entries.toTypedArray()) { transition ->
        val isSelected = selectedClip.transitionType == transition
        Surface(
          onClick = { onUpdateClip(selectedClip.copy(transitionType = transition)) },
          shape = RoundedCornerShape(8.dp),
          color = if (isSelected) AkPurplePrimary else AkBgSurfaceElevated,
          modifier = Modifier.border(
            1.dp,
            if (isSelected) AkCyanAccent else Color.Transparent,
            RoundedCornerShape(8.dp)
          )
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text(
              text = transition.displayName,
              color = AkTextPrimary,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          }
        }
      }
    }

    if (selectedClip.transitionType != TransitionType.NONE) {
      Spacer(modifier = Modifier.height(10.dp))

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Transition Duration", color = AkTextPrimary, fontSize = 12.sp)
        Text("${selectedClip.transitionDurationMs / 1000f}s", color = AkCyanAccent, fontSize = 12.sp)
      }
      Slider(
        value = selectedClip.transitionDurationMs.toFloat(),
        onValueChange = { onUpdateClip(selectedClip.copy(transitionDurationMs = it.toLong())) },
        valueRange = 200f..2000f,
        colors = SliderDefaults.colors(thumbColor = AkCyanAccent, activeTrackColor = AkPurplePrimary)
      )
    }
  }
}
