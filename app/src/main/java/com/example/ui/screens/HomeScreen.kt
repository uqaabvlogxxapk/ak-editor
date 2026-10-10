package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AuthRepository
import com.example.model.TemplateLibrary
import com.example.model.VideoProject
import com.example.model.VideoTemplate
import com.example.ui.components.SubscriptionDialog
import com.example.ui.editor.formatTimecode
import com.example.ui.theme.AkBgDark
import com.example.ui.theme.AkBgSurface
import com.example.ui.theme.AkBgSurfaceElevated
import com.example.ui.theme.AkCyanAccent
import com.example.ui.theme.AkPurpleLight
import com.example.ui.theme.AkPurplePrimary
import com.example.ui.theme.AkSuccess
import com.example.ui.theme.AkTextMuted
import com.example.ui.theme.AkTextPrimary
import com.example.ui.theme.AkTextSecondary
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
  projects: List<VideoProject>,
  currentUser: FirebaseUser?,
  isProUser: Boolean,
  authRepository: AuthRepository,
  onOpenProject: (VideoProject) -> Unit,
  onDeleteProject: (String) -> Unit,
  onToggleProEntitlement: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  var showNewProjectDialog by remember { mutableStateOf(false) }
  var showSubscriptionDialog by remember { mutableStateOf(false) }
  var isSigningIn by remember { mutableStateOf(false) }
  var signInError by remember { mutableStateOf<String?>(null) }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = AkBgDark
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(8.dp))
        // Top Header Branding & Pro Button
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "AK EDITOR",
              color = AkTextPrimary,
              fontWeight = FontWeight.Black,
              fontSize = 24.sp,
              letterSpacing = 1.sp
            )
            Text(
              text = "PROFESSIONAL AI VIDEO SUITE",
              color = AkCyanAccent,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp,
              letterSpacing = 1.5.sp
            )
          }

          // Pro Badge or Upgrade button
          Surface(
            onClick = { showSubscriptionDialog = true },
            shape = RoundedCornerShape(20.dp),
            color = if (isProUser) Color(0xFF78350F) else Color(0xFF262145),
            modifier = Modifier
              .border(
                1.dp,
                if (isProUser) Color(0xFFF59E0B) else AkPurplePrimary,
                RoundedCornerShape(20.dp)
              )
              .testTag("pro_subscription_button")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFF59E0B),
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (isProUser) "PRO PLAN" else "UPGRADE",
                color = AkTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }
          }
        }
      }

      // User Auth & Cloud Account Card
      item {
        UserAccountCard(
          currentUser = currentUser,
          isProUser = isProUser,
          isSigningIn = isSigningIn,
          signInError = signInError,
          onSignIn = {
            scope.launch {
              isSigningIn = true
              signInError = null
              val result = authRepository.signInWithGoogle(context)
              result.onSuccess { user ->
                authRepository.syncUserProfile(context, user)
              }.onFailure { err ->
                signInError = err.message ?: "Sign-in cancelled"
              }
              isSigningIn = false
            }
          },
          onSignOut = {
            authRepository.signOut()
          }
        )
      }

      // Primary Quick Action Banner: Start New Project
      item {
        Surface(
          onClick = { showNewProjectDialog = true },
          shape = RoundedCornerShape(14.dp),
          color = Color.Transparent,
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
              Brush.horizontalGradient(
                listOf(Color(0xFF6D28D9), Color(0xFF0284C7))
              )
            )
            .testTag("create_new_project_banner")
        ) {
          Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Start New Project",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "16:9, 9:16 Reels, 4K Timeline & Multi-Track",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp
              )
            }

            Box(
              modifier = Modifier
                .size(44.dp)
                .background(Color.White, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Add, contentDescription = null, tint = AkPurplePrimary, modifier = Modifier.size(24.dp))
            }
          }
        }
      }

      // Premium Templates Carousel
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Cinematic Templates", color = AkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
          Text("${TemplateLibrary.templates.size} Available", color = AkTextSecondary, fontSize = 11.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          items(TemplateLibrary.templates) { template ->
            TemplateCard(
              template = template,
              onSelect = {
                val proj = template.createProject()
                onOpenProject(proj)
              }
            )
          }
        }
      }

      // Recent Projects Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("My Video Projects", color = AkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
          Text("${projects.size} Saved", color = AkTextSecondary, fontSize = 11.sp)
        }
      }

      // Recent Projects Cards
      if (projects.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(130.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(AkBgSurfaceElevated),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = AkTextMuted, modifier = Modifier.size(32.dp))
              Spacer(modifier = Modifier.height(6.dp))
              Text("No projects yet", color = AkTextSecondary, fontSize = 13.sp)
              Text("Tap 'Start New Project' or pick a template above", color = AkTextMuted, fontSize = 11.sp)
            }
          }
        }
      } else {
        items(projects) { project ->
          ProjectListItem(
            project = project,
            onOpen = { onOpenProject(project) },
            onDelete = { onDeleteProject(project.id) }
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }

  if (showNewProjectDialog) {
    NewProjectDialog(
      onDismiss = { showNewProjectDialog = false },
      onCreateProject = { proj ->
        showNewProjectDialog = false
        onOpenProject(proj)
      }
    )
  }

  if (showSubscriptionDialog) {
    SubscriptionDialog(
      isProUser = isProUser,
      onToggleProEntitlement = onToggleProEntitlement,
      onDismiss = { showSubscriptionDialog = false }
    )
  }
}

@Composable
private fun UserAccountCard(
  currentUser: FirebaseUser?,
  isProUser: Boolean,
  isSigningIn: Boolean,
  signInError: String?,
  onSignIn: () -> Unit,
  onSignOut: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = AkBgSurface,
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, Color(0xFF262145), RoundedCornerShape(12.dp))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (currentUser != null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .background(AkPurplePrimary, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = (currentUser.displayName?.take(1) ?: "U").uppercase(),
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Text(
              text = currentUser.displayName ?: "Creator",
              color = AkTextPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CloudDone, contentDescription = null, tint = AkSuccess, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Firestore Cloud Sync Active",
                color = AkSuccess,
                fontSize = 10.sp
              )
            }
          }
        }

        OutlinedButton(
          onClick = onSignOut,
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("sign_out_button")
        ) {
          Text("Sign Out", fontSize = 11.sp, color = AkTextSecondary)
        }
      } else {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Cloud Project Sync & Backup",
            color = AkTextPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
          )
          Text(
            text = "Sign in to backup projects across devices",
            color = AkTextSecondary,
            fontSize = 11.sp
          )
          if (signInError != null) {
            Text(text = signInError, color = Color(0xFFEF4444), fontSize = 10.sp)
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
          onClick = onSignIn,
          colors = ButtonDefaults.buttonColors(containerColor = AkPurplePrimary),
          shape = RoundedCornerShape(8.dp),
          enabled = !isSigningIn,
          modifier = Modifier.testTag("google_sign_in_button")
        ) {
          if (isSigningIn) {
            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 2.dp)
          } else {
            Text("Google Sign-In", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
private fun TemplateCard(
  template: VideoTemplate,
  onSelect: () -> Unit
) {
  Surface(
    onClick = onSelect,
    shape = RoundedCornerShape(10.dp),
    color = AkBgSurfaceElevated,
    modifier = Modifier
      .width(150.dp)
      .border(1.dp, Color(0xFF262145), RoundedCornerShape(10.dp))
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(55.dp)
          .clip(RoundedCornerShape(6.dp))
          .background(
            Brush.linearGradient(
              listOf(Color(0xFF4C1D95), Color(0xFF0E7490))
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = template.aspectRatio.label,
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp
        )
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(template.name, color = AkTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
      Text(template.category, color = AkCyanAccent, fontSize = 10.sp)

      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "Use Template",
        color = AkPurpleLight,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

@Composable
private fun ProjectListItem(
  project: VideoProject,
  onOpen: () -> Unit,
  onDelete: () -> Unit
) {
  Surface(
    onClick = onOpen,
    shape = RoundedCornerShape(10.dp),
    color = AkBgSurface,
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, Color(0xFF262145), RoundedCornerShape(10.dp))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF312E81)),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Movie, contentDescription = null, tint = AkCyanAccent, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(project.title, color = AkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "${project.aspectRatio.label} • ${project.resolution.label.take(5)} • ${formatTimecode(project.durationMs)}",
              color = AkTextSecondary,
              fontSize = 11.sp
            )
          }
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onOpen) {
          Icon(Icons.Default.Edit, contentDescription = "Edit Project", tint = AkCyanAccent, modifier = Modifier.size(18.dp))
        }
        IconButton(onClick = onDelete) {
          Icon(Icons.Default.Delete, contentDescription = "Delete Project", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
        }
      }
    }
  }
}
