package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AkBgSurface
import com.example.ui.theme.AkBgSurfaceElevated
import com.example.ui.theme.AkCyanAccent
import com.example.ui.theme.AkPurplePrimary
import com.example.ui.theme.AkSuccess
import com.example.ui.theme.AkTextMuted
import com.example.ui.theme.AkTextPrimary
import com.example.ui.theme.AkTextSecondary

@Composable
fun SubscriptionDialog(
  isProUser: Boolean,
  onToggleProEntitlement: (Boolean) -> Unit,
  onDismiss: () -> Unit
) {
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
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("AK EDITOR PRO", color = AkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = AkTextSecondary)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          "Unlock ultimate video creator superpowers with uncompressed 4K rendering and advanced AI models.",
          color = AkTextSecondary,
          fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        val proPerks = listOf(
          "Export up to 4K 2160p UHD resolution",
          "Ultra-smooth 60 FPS frame rates",
          "AI Speech-to-Text Auto Captions",
          "AI Subject Background Removal & Masking",
          "Advanced Camera & Motion Tracking",
          "10+ Premium Cinematic Templates",
          "Unlimited Firestore Cloud Sync & Backup",
          "100% Clean No Watermark guarantee"
        )

        proPerks.forEach { perk ->
          Row(
            modifier = Modifier.padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = AkSuccess, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(perk, color = AkTextPrimary, fontSize = 12.sp)
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Entitlement Status Badge & Toggle
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AkBgSurfaceElevated)
            .padding(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Current Status", color = AkTextSecondary, fontSize = 11.sp)
              Text(
                if (isProUser) "PRO ACTIVE" else "FREE TIER",
                color = if (isProUser) Color(0xFFF59E0B) else AkCyanAccent,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            }

            Button(
              onClick = {
                onToggleProEntitlement(!isProUser)
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isProUser) Color(0xFF475569) else Color(0xFFD97706)
              ),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("toggle_pro_button")
            ) {
              Text(
                text = if (isProUser) "Switch to Free" else "Upgrade to Pro (Dev Entitlement)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }
  }
}
