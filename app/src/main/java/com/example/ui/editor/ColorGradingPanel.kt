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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ColorFilterType
import com.example.model.TimelineClip
import com.example.ui.theme.AkBgSurface
import com.example.ui.theme.AkBgSurfaceElevated
import com.example.ui.theme.AkCyanAccent
import com.example.ui.theme.AkPurplePrimary
import com.example.ui.theme.AkTextMuted
import com.example.ui.theme.AkTextPrimary
import com.example.ui.theme.AkTextSecondary

@Composable
fun ColorGradingPanel(
  selectedClip: TimelineClip?,
  onUpdateClip: (TimelineClip) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(AkBgSurface)
      .padding(12.dp)
      .verticalScroll(rememberScrollState())
  ) {
    if (selectedClip == null) {
      Text(
        text = "Select a clip on the timeline to adjust color grading and filters.",
        color = AkTextMuted,
        fontSize = 13.sp
      )
      return@Column
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Filters & Color Grading",
        color = AkTextPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
      )

      Button(
        onClick = {
          onUpdateClip(
            selectedClip.copy(
              colorFilter = ColorFilterType.NONE,
              brightness = 0f,
              contrast = 1.0f,
              saturation = 1.0f,
              temperature = 0f,
              vignette = 0f
            )
          )
        },
        colors = ButtonDefaults.buttonColors(containerColor = AkBgSurfaceElevated),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.testTag("reset_grading_button")
      ) {
        Text("Reset", color = AkTextSecondary, fontSize = 11.sp)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Preset Filter Chips
    Text("Cinematic Presets:", color = AkTextSecondary, fontSize = 11.sp)
    Spacer(modifier = Modifier.height(6.dp))

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      items(ColorFilterType.entries.toTypedArray()) { filter ->
        val isSelected = selectedClip.colorFilter == filter
        Surface(
          onClick = { onUpdateClip(selectedClip.copy(colorFilter = filter)) },
          shape = RoundedCornerShape(8.dp),
          color = if (isSelected) AkPurplePrimary else AkBgSurfaceElevated,
          modifier = Modifier.border(
            1.dp,
            if (isSelected) AkCyanAccent else Color.Transparent,
            RoundedCornerShape(8.dp)
          )
        ) {
          Text(
            text = filter.displayName,
            color = AkTextPrimary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    Text("Pro Adjustment Sliders:", color = AkTextSecondary, fontSize = 11.sp)
    Spacer(modifier = Modifier.height(8.dp))

    // Brightness / Exposure Slider
    GradingSlider(
      label = "Brightness / Exposure",
      value = selectedClip.brightness,
      valueRange = -0.5f..0.5f,
      valueDisplay = String.format("%+.2f", selectedClip.brightness),
      onValueChange = { onUpdateClip(selectedClip.copy(brightness = it)) }
    )

    // Contrast Slider
    GradingSlider(
      label = "Contrast",
      value = selectedClip.contrast,
      valueRange = 0.5f..2.0f,
      valueDisplay = String.format("%.2fx", selectedClip.contrast),
      onValueChange = { onUpdateClip(selectedClip.copy(contrast = it)) }
    )

    // Saturation Slider
    GradingSlider(
      label = "Saturation",
      value = selectedClip.saturation,
      valueRange = 0.0f..2.0f,
      valueDisplay = String.format("%.2fx", selectedClip.saturation),
      onValueChange = { onUpdateClip(selectedClip.copy(saturation = it)) }
    )

    // Temperature (Warm vs Cool) Slider
    GradingSlider(
      label = "Temperature (Cool / Warm)",
      value = selectedClip.temperature,
      valueRange = -1.0f..1.0f,
      valueDisplay = if (selectedClip.temperature > 0.05f) "Warm +${String.format("%.2f", selectedClip.temperature)}"
      else if (selectedClip.temperature < -0.05f) "Cool ${String.format("%.2f", selectedClip.temperature)}"
      else "Neutral",
      onValueChange = { onUpdateClip(selectedClip.copy(temperature = it)) }
    )

    // Vignette Slider
    GradingSlider(
      label = "Vignette Border",
      value = selectedClip.vignette,
      valueRange = 0.0f..1.0f,
      valueDisplay = "${(selectedClip.vignette * 100).toInt()}%",
      onValueChange = { onUpdateClip(selectedClip.copy(vignette = it)) }
    )
  }
}

@Composable
private fun GradingSlider(
  label: String,
  value: Float,
  valueRange: ClosedFloatingPointRange<Float>,
  valueDisplay: String,
  onValueChange: (Float) -> Unit
) {
  Column(modifier = Modifier.padding(vertical = 4.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(label, color = AkTextPrimary, fontSize = 12.sp)
      Text(valueDisplay, color = AkCyanAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
    Slider(
      value = value,
      onValueChange = onValueChange,
      valueRange = valueRange,
      colors = SliderDefaults.colors(
        thumbColor = AkCyanAccent,
        activeTrackColor = AkPurplePrimary,
        inactiveTrackColor = Color(0xFF262145)
      )
    )
  }
}
