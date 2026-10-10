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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TextAnimationType
import com.example.model.TextOverlay
import com.example.ui.theme.AkBgSurface
import com.example.ui.theme.AkBgSurfaceElevated
import com.example.ui.theme.AkBgSurfaceVariant
import com.example.ui.theme.AkCyanAccent
import com.example.ui.theme.AkPurplePrimary
import com.example.ui.theme.AkTextMuted
import com.example.ui.theme.AkTextPrimary
import com.example.ui.theme.AkTextSecondary
import java.util.UUID

@Composable
fun TextTitlesPanel(
  textOverlays: List<TextOverlay>,
  onAddTextOverlay: (TextOverlay) -> Unit,
  onUpdateTextOverlay: (TextOverlay) -> Unit,
  onDeleteTextOverlay: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTextId by remember { mutableStateOf(textOverlays.firstOrNull()?.id) }
  val activeOverlay = textOverlays.firstOrNull { it.id == selectedTextId } ?: textOverlays.firstOrNull()

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(AkBgSurface)
      .padding(12.dp)
      .verticalScroll(rememberScrollState())
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("Text Styles & Motion Titles", color = AkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)

      Button(
        onClick = {
          val newOverlay = TextOverlay(
            id = UUID.randomUUID().toString(),
            text = "NEW TITLE",
            startTimeMs = 0L,
            durationMs = 4000L,
            fontSize = 28,
            textColorHex = "#FFFFFF",
            animationType = TextAnimationType.FADE_IN
          )
          onAddTextOverlay(newOverlay)
          selectedTextId = newOverlay.id
        },
        colors = ButtonDefaults.buttonColors(containerColor = AkPurplePrimary),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.testTag("add_title_button")
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Add Title", fontSize = 12.sp)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Text Overlays selector chips
    if (textOverlays.isNotEmpty()) {
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(textOverlays) { overlay ->
          val isSelected = overlay.id == activeOverlay?.id
          Surface(
            onClick = { selectedTextId = overlay.id },
            shape = RoundedCornerShape(6.dp),
            color = if (isSelected) AkPurplePrimary else AkBgSurfaceElevated,
            modifier = Modifier.border(1.dp, if (isSelected) AkCyanAccent else Color.Transparent, RoundedCornerShape(6.dp))
          ) {
            Text(
              text = overlay.text.take(12),
              color = AkTextPrimary,
              fontSize = 11.sp,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
            )
          }
        }
      }
    }

    if (activeOverlay != null) {
      Spacer(modifier = Modifier.height(12.dp))

      // Text Input Field
      OutlinedTextField(
        value = activeOverlay.text,
        onValueChange = { onUpdateTextOverlay(activeOverlay.copy(text = it)) },
        label = { Text("Title Content") },
        modifier = Modifier.fillMaxWidth().testTag("title_content_input"),
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = AkTextPrimary,
          unfocusedTextColor = AkTextPrimary,
          focusedBorderColor = AkCyanAccent,
          unfocusedBorderColor = Color(0xFF332D59)
        )
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Motion Animations
      Text("Entrance & Motion Animation:", color = AkTextSecondary, fontSize = 11.sp)
      Spacer(modifier = Modifier.height(6.dp))

      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(TextAnimationType.entries.toTypedArray()) { anim ->
          val isSelected = activeOverlay.animationType == anim
          Surface(
            onClick = { onUpdateTextOverlay(activeOverlay.copy(animationType = anim)) },
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) AkPurplePrimary else AkBgSurfaceElevated,
            modifier = Modifier.border(1.dp, if (isSelected) AkCyanAccent else Color.Transparent, RoundedCornerShape(8.dp))
          ) {
            Text(
              text = anim.displayName,
              color = AkTextPrimary,
              fontSize = 11.sp,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Font Size Slider
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Font Size", color = AkTextPrimary, fontSize = 12.sp)
        Text("${activeOverlay.fontSize}sp", color = AkCyanAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
      Slider(
        value = activeOverlay.fontSize.toFloat(),
        onValueChange = { onUpdateTextOverlay(activeOverlay.copy(fontSize = it.toInt())) },
        valueRange = 16f..54f,
        colors = SliderDefaults.colors(thumbColor = AkCyanAccent, activeTrackColor = AkPurplePrimary)
      )

      // Color Palette Selection
      Text("Text Color:", color = AkTextSecondary, fontSize = 11.sp)
      Spacer(modifier = Modifier.height(6.dp))
      val colors = listOf("#FFFFFF", "#06B6D4", "#8B5CF6", "#FBBF24", "#EF4444", "#10B981")
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        colors.forEach { hex ->
          val isSelected = activeOverlay.textColorHex == hex
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(Color(android.graphics.Color.parseColor(hex)))
              .border(2.dp, if (isSelected) AkCyanAccent else Color.Transparent, CircleShape)
              .clickable { onUpdateTextOverlay(activeOverlay.copy(textColorHex = hex)) }
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Stroke & Box toggles
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Text Outline Stroke", color = AkTextPrimary, fontSize = 12.sp)
        Switch(
          checked = activeOverlay.hasStroke,
          onCheckedChange = { onUpdateTextOverlay(activeOverlay.copy(hasStroke = it)) },
          colors = SwitchDefaults.colors(checkedThumbColor = AkCyanAccent, checkedTrackColor = AkPurplePrimary)
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Dark Background Box", color = AkTextPrimary, fontSize = 12.sp)
        Switch(
          checked = activeOverlay.hasBackground,
          onCheckedChange = { onUpdateTextOverlay(activeOverlay.copy(hasBackground = it)) },
          colors = SwitchDefaults.colors(checkedThumbColor = AkCyanAccent, checkedTrackColor = AkPurplePrimary)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Delete Title Button
      OutlinedButton(
        onClick = { onDeleteTextOverlay(activeOverlay.id) },
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Delete This Title", fontSize = 12.sp)
      }
    }
  }
}
