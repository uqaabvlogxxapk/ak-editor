package com.example.ui.editor

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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MaskType
import com.example.model.TimelineClip
import com.example.ui.theme.AkBgSurface
import com.example.ui.theme.AkBgSurfaceElevated
import com.example.ui.theme.AkCyanAccent
import com.example.ui.theme.AkPurplePrimary
import com.example.ui.theme.AkTextMuted
import com.example.ui.theme.AkTextPrimary
import com.example.ui.theme.AkTextSecondary

@Composable
fun ChromaKeyMaskPanel(
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
      Text("Select a clip on the timeline to configure Chroma Key and Layer Masking.", color = AkTextMuted, fontSize = 13.sp)
      return@Column
    }

    // SECTION 1: CHROMA KEY (GREEN SCREEN)
    Text("Chroma Key (Color Keying)", color = AkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    Spacer(modifier = Modifier.height(8.dp))

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
        Text("Enable Chroma Key", color = AkTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Text("Keys out chosen background color", color = AkTextSecondary, fontSize = 11.sp)
      }
      Switch(
        checked = selectedClip.chromaKeyEnabled,
        onCheckedChange = { onUpdateClip(selectedClip.copy(chromaKeyEnabled = it)) },
        colors = SwitchDefaults.colors(checkedThumbColor = AkCyanAccent, checkedTrackColor = AkPurplePrimary)
      )
    }

    if (selectedClip.chromaKeyEnabled) {
      Spacer(modifier = Modifier.height(10.dp))
      Text("Key Color:", color = AkTextSecondary, fontSize = 11.sp)
      Spacer(modifier = Modifier.height(6.dp))

      val colors = listOf(
        "#00FF00" to "Green Screen",
        "#0000FF" to "Blue Screen",
        "#FF00FF" to "Magenta Key",
        "#000000" to "Luma Key Black"
      )

      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        colors.forEach { (hex, label) ->
          val isSelected = selectedClip.chromaKeyColorHex == hex
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onUpdateClip(selectedClip.copy(chromaKeyColorHex = hex)) }
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(android.graphics.Color.parseColor(hex)))
                .border(
                  2.dp,
                  if (isSelected) AkCyanAccent else Color(0xFF475569),
                  CircleShape
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(label.take(5), color = AkTextSecondary, fontSize = 10.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Tolerance Slider
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Color Tolerance", color = AkTextPrimary, fontSize = 12.sp)
        Text("${(selectedClip.chromaKeyTolerance * 100).toInt()}%", color = AkCyanAccent, fontSize = 12.sp)
      }
      Slider(
        value = selectedClip.chromaKeyTolerance,
        onValueChange = { onUpdateClip(selectedClip.copy(chromaKeyTolerance = it)) },
        valueRange = 0.1f..0.9f,
        colors = SliderDefaults.colors(thumbColor = AkCyanAccent, activeTrackColor = AkPurplePrimary)
      )

      // Softness Slider
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Edge Softness", color = AkTextPrimary, fontSize = 12.sp)
        Text("${(selectedClip.chromaKeySoftness * 100).toInt()}%", color = AkCyanAccent, fontSize = 12.sp)
      }
      Slider(
        value = selectedClip.chromaKeySoftness,
        onValueChange = { onUpdateClip(selectedClip.copy(chromaKeySoftness = it)) },
        valueRange = 0.0f..0.5f,
        colors = SliderDefaults.colors(thumbColor = AkCyanAccent, activeTrackColor = AkPurplePrimary)
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // SECTION 2: LAYER MASKING
    Text("Layer Masking", color = AkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    Spacer(modifier = Modifier.height(8.dp))

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      items(MaskType.entries.toTypedArray()) { mask ->
        val isSelected = selectedClip.maskType == mask
        Surface(
          onClick = { onUpdateClip(selectedClip.copy(maskType = mask)) },
          shape = RoundedCornerShape(8.dp),
          color = if (isSelected) AkPurplePrimary else AkBgSurfaceElevated,
          modifier = Modifier.border(
            1.dp,
            if (isSelected) AkCyanAccent else Color.Transparent,
            RoundedCornerShape(8.dp)
          )
        ) {
          Text(
            text = mask.displayName,
            color = AkTextPrimary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          )
        }
      }
    }

    if (selectedClip.maskType != MaskType.NONE) {
      Spacer(modifier = Modifier.height(10.dp))

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Mask Scale", color = AkTextPrimary, fontSize = 12.sp)
        Text("${String.format("%.2f", selectedClip.maskScale)}x", color = AkCyanAccent, fontSize = 12.sp)
      }
      Slider(
        value = selectedClip.maskScale,
        onValueChange = { onUpdateClip(selectedClip.copy(maskScale = it)) },
        valueRange = 0.3f..1.8f,
        colors = SliderDefaults.colors(thumbColor = AkCyanAccent, activeTrackColor = AkPurplePrimary)
      )

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(AkBgSurfaceElevated)
          .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Invert Mask Cutout", color = AkTextPrimary, fontSize = 12.sp)
        Switch(
          checked = selectedClip.maskInverted,
          onCheckedChange = { onUpdateClip(selectedClip.copy(maskInverted = it)) },
          colors = SwitchDefaults.colors(checkedThumbColor = AkCyanAccent, checkedTrackColor = AkPurplePrimary)
        )
      }
    }
  }
}
