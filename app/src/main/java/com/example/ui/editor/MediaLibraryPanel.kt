package com.example.ui.editor

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SampleMediaItem
import com.example.data.SampleMediaLibrary
import com.example.model.ClipType
import com.example.model.TimelineClip
import com.example.ui.theme.AkBgSurface
import com.example.ui.theme.AkBgSurfaceElevated
import com.example.ui.theme.AkCyanAccent
import com.example.ui.theme.AkPurplePrimary
import com.example.ui.theme.AkTextMuted
import com.example.ui.theme.AkTextPrimary
import com.example.ui.theme.AkTextSecondary

@Composable
fun MediaLibraryPanel(
  onAddClip: (TimelineClip) -> Unit,
  modifier: Modifier = Modifier
) {
  // Modern Android Zero-Permission Photo / Video Picker
  val mediaPicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      val importedClip = TimelineClip(
        name = "Imported Media",
        type = ClipType.VIDEO,
        trackIndex = 0,
        startTimeMs = 0L,
        durationMs = 6000L,
        localUri = uri.toString()
      )
      onAddClip(importedClip)
    }
  }

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
      Column {
        Text("Media Library & Import", color = AkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text("Import local footage or insert 4K studio stock", color = AkTextSecondary, fontSize = 11.sp)
      }

      Button(
        onClick = {
          mediaPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        },
        colors = ButtonDefaults.buttonColors(containerColor = AkCyanAccent),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.testTag("import_device_media_button")
      ) {
        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Import File", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    Text("Studio Stock Footage Clips:", color = AkTextSecondary, fontSize = 11.sp)
    Spacer(modifier = Modifier.height(6.dp))

    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
      items(SampleMediaLibrary.videoItems) { media ->
        StockMediaCard(
          media = media,
          onAddAsMain = {
            onAddClip(
              TimelineClip(
                name = media.title,
                type = ClipType.VIDEO,
                trackIndex = 0,
                durationMs = media.durationMs,
                sampleAssetId = media.id
              )
            )
          },
          onAddAsPip = {
            onAddClip(
              TimelineClip(
                name = "${media.title} (PIP)",
                type = ClipType.VIDEO,
                trackIndex = 1,
                durationMs = media.durationMs,
                sampleAssetId = media.id
              )
            )
          }
        )
      }
    }
  }
}

@Composable
private fun StockMediaCard(
  media: SampleMediaItem,
  onAddAsMain: () -> Unit,
  onAddAsPip: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = AkBgSurfaceElevated,
    modifier = Modifier
      .width(160.dp)
      .border(1.dp, Color(0xFF262145), RoundedCornerShape(8.dp))
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      // Color Preview Thumbnail
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(55.dp)
          .clip(RoundedCornerShape(6.dp))
          .background(Color(android.graphics.Color.parseColor(media.dominantColorHex))),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "${media.durationMs / 1000}s",
          color = Color.White,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier
            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(media.title, color = AkTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
      Text(media.description, color = AkTextMuted, fontSize = 10.sp, maxLines = 2)

      Spacer(modifier = Modifier.height(8.dp))

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Surface(
          onClick = onAddAsMain,
          shape = RoundedCornerShape(4.dp),
          color = AkPurplePrimary,
          modifier = Modifier.weight(1f)
        ) {
          Row(
            modifier = Modifier.padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
            Text("V1", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.width(4.dp))

        Surface(
          onClick = onAddAsPip,
          shape = RoundedCornerShape(4.dp),
          color = Color(0xFF332D59),
          modifier = Modifier.weight(1f)
        ) {
          Row(
            modifier = Modifier.padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.PictureInPicture, contentDescription = null, tint = AkCyanAccent, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text("PIP", color = AkCyanAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
