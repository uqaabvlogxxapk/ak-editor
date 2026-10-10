package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.GeminiAiService
import com.example.model.TimelineClip
import com.example.model.VideoProject
import com.example.ui.components.SubscriptionDialog
import com.example.ui.editor.AiToolsPanel
import com.example.ui.editor.AudioStudioPanel
import com.example.ui.editor.ChromaKeyMaskPanel
import com.example.ui.editor.ColorGradingPanel
import com.example.ui.editor.ExportDialog
import com.example.ui.editor.MediaLibraryPanel
import com.example.ui.editor.TextTitlesPanel
import com.example.ui.editor.TimelineView
import com.example.ui.editor.TransitionsPanel
import com.example.ui.editor.VideoPreviewCanvas
import com.example.ui.theme.AkBgDark
import com.example.ui.theme.AkBgSurface
import com.example.ui.theme.AkCyanAccent
import com.example.ui.theme.AkPurplePrimary
import com.example.ui.theme.AkSuccess
import com.example.ui.theme.AkTextMuted
import com.example.ui.theme.AkTextPrimary
import com.example.ui.theme.AkTextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun EditorScreen(
  initialProject: VideoProject,
  isProUser: Boolean,
  aiService: GeminiAiService,
  onSaveProject: (VideoProject) -> Unit,
  onToggleProEntitlement: (Boolean) -> Unit,
  onBackToHome: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scope = rememberCoroutineScope()
  var currentProject by remember { mutableStateOf(initialProject) }
  var currentPlayheadMs by remember { mutableLongStateOf(0L) }
  var isPlaying by remember { mutableStateOf(false) }
  var selectedClipId by remember { mutableStateOf<String?>(initialProject.clips.firstOrNull()?.id) }
  var activeToolTab by remember { mutableIntStateOf(0) } // 0: AI Tools, 1: Color/Filters, 2: Chroma/Mask, 3: Audio, 4: Titles, 5: Transitions, 6: Media
  var showSplitPreview by remember { mutableStateOf(false) }

  var showExportDialog by remember { mutableStateOf(false) }
  var showSubscriptionDialog by remember { mutableStateOf(false) }
  var cloudSavedNotice by remember { mutableStateOf(false) }

  // Undo & Redo History
  val undoStack = remember { mutableStateListOf<VideoProject>() }
  val redoStack = remember { mutableStateListOf<VideoProject>() }

  fun pushState(newProj: VideoProject) {
    undoStack.add(currentProject)
    redoStack.clear()
    currentProject = newProj
  }

  fun performUndo() {
    if (undoStack.isNotEmpty()) {
      val prev = undoStack.removeAt(undoStack.lastIndex)
      redoStack.add(currentProject)
      currentProject = prev
    }
  }

  fun performRedo() {
    if (redoStack.isNotEmpty()) {
      val next = redoStack.removeAt(redoStack.lastIndex)
      undoStack.add(currentProject)
      currentProject = next
    }
  }

  // Back navigation handling
  BackHandler {
    onSaveProject(currentProject)
    onBackToHome()
  }

  // Playback Loop
  LaunchedEffect(isPlaying) {
    if (isPlaying) {
      val frameStepMs = 33L // ~30 FPS preview updates
      while (isPlaying) {
        delay(frameStepMs)
        val nextTime = currentPlayheadMs + frameStepMs
        if (nextTime >= currentProject.durationMs) {
          currentPlayheadMs = 0L
          isPlaying = false
        } else {
          currentPlayheadMs = nextTime
        }
      }
    }
  }

  val selectedClip = currentProject.clips.firstOrNull { it.id == selectedClipId }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = AkBgDark
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      // 1. Top Editor Navigation Bar
      EditorTopBar(
        project = currentProject,
        canUndo = undoStack.isNotEmpty(),
        canRedo = redoStack.isNotEmpty(),
        cloudSavedNotice = cloudSavedNotice,
        onBack = {
          onSaveProject(currentProject)
          onBackToHome()
        },
        onUndo = { performUndo() },
        onRedo = { performRedo() },
        onSave = {
          onSaveProject(currentProject)
          cloudSavedNotice = true
          scope.launch {
            delay(2000)
            cloudSavedNotice = false
          }
        },
        onExport = { showExportDialog = true }
      )

      // 2. Video Preview Canvas
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(0.42f)
          .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
      ) {
        VideoPreviewCanvas(
          project = currentProject,
          currentPlayheadMs = currentPlayheadMs,
          isPlaying = isPlaying,
          showBeforeAfterSplit = showSplitPreview
        )
      }

      // 3. Multi-Track Timeline
      TimelineView(
        project = currentProject,
        currentPlayheadMs = currentPlayheadMs,
        isPlaying = isPlaying,
        selectedClipId = selectedClipId,
        onPlayheadChanged = { currentPlayheadMs = it },
        onTogglePlay = { isPlaying = !isPlaying },
        onSelectClip = { selectedClipId = it },
        onSplitClip = {
          if (selectedClip != null && currentPlayheadMs > selectedClip.startTimeMs && currentPlayheadMs < (selectedClip.startTimeMs + selectedClip.durationMs)) {
            val offsetInClip = currentPlayheadMs - selectedClip.startTimeMs
            val firstHalf = selectedClip.copy(
              durationMs = offsetInClip
            )
            val secondHalf = selectedClip.copy(
              id = UUID.randomUUID().toString(),
              name = "${selectedClip.name} (Part 2)",
              startTimeMs = currentPlayheadMs,
              durationMs = selectedClip.durationMs - offsetInClip
            )
            val updatedClips = currentProject.clips.map {
              if (it.id == selectedClip.id) firstHalf else it
            } + secondHalf
            pushState(currentProject.copy(clips = updatedClips))
            selectedClipId = secondHalf.id
          }
        },
        onDeleteClip = { clipId ->
          val updated = currentProject.clips.filter { it.id != clipId }
          pushState(currentProject.copy(clips = updated))
          selectedClipId = updated.firstOrNull()?.id
        },
        onDuplicateClip = { clipId ->
          val clipToCopy = currentProject.clips.firstOrNull { it.id == clipId }
          if (clipToCopy != null) {
            val duplicated = clipToCopy.copy(
              id = UUID.randomUUID().toString(),
              name = "${clipToCopy.name} (Copy)",
              startTimeMs = clipToCopy.startTimeMs + clipToCopy.durationMs
            )
            pushState(currentProject.copy(clips = currentProject.clips + duplicated))
            selectedClipId = duplicated.id
          }
        },
        onTrimClip = { clipId, startMs, durMs ->
          val updated = currentProject.clips.map {
            if (it.id == clipId) it.copy(startTimeMs = startMs, durationMs = durMs) else it
          }
          pushState(currentProject.copy(clips = updated))
        },
        modifier = Modifier.fillMaxWidth()
      )

      // 4. Tabbed Inspector Tool Bar
      EditorToolTabs(
        activeTab = activeToolTab,
        onSelectTab = { activeToolTab = it }
      )

      // 5. Active Tool Inspector Panel
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(0.38f)
      ) {
        when (activeToolTab) {
          0 -> AiToolsPanel(
            project = currentProject,
            selectedClip = selectedClip,
            aiService = aiService,
            onUpdateSubtitles = { subs ->
              pushState(currentProject.copy(subtitles = subs))
            },
            onUpdateClip = { updated ->
              val list = currentProject.clips.map { if (it.id == updated.id) updated else it }
              pushState(currentProject.copy(clips = list))
            },
            onUpdateTracking = { tracking ->
              pushState(currentProject.copy(motionTracking = tracking))
            },
            onToggleSplitPreview = { showSplitPreview = !showSplitPreview },
            showSplitPreview = showSplitPreview
          )
          1 -> ColorGradingPanel(
            selectedClip = selectedClip,
            onUpdateClip = { updated ->
              val list = currentProject.clips.map { if (it.id == updated.id) updated else it }
              pushState(currentProject.copy(clips = list))
            }
          )
          2 -> ChromaKeyMaskPanel(
            selectedClip = selectedClip,
            onUpdateClip = { updated ->
              val list = currentProject.clips.map { if (it.id == updated.id) updated else it }
              pushState(currentProject.copy(clips = list))
            }
          )
          3 -> AudioStudioPanel(
            project = currentProject,
            selectedClip = selectedClip,
            onUpdateClip = { updated ->
              val list = currentProject.clips.map { if (it.id == updated.id) updated else it }
              pushState(currentProject.copy(clips = list))
            },
            onAddAudioTrack = { audioClip ->
              pushState(currentProject.copy(clips = currentProject.clips + audioClip))
            }
          )
          4 -> TextTitlesPanel(
            textOverlays = currentProject.textOverlays,
            onAddTextOverlay = { newOverlay ->
              pushState(currentProject.copy(textOverlays = currentProject.textOverlays + newOverlay))
            },
            onUpdateTextOverlay = { updated ->
              val list = currentProject.textOverlays.map { if (it.id == updated.id) updated else it }
              pushState(currentProject.copy(textOverlays = list))
            },
            onDeleteTextOverlay = { delId ->
              val list = currentProject.textOverlays.filter { it.id != delId }
              pushState(currentProject.copy(textOverlays = list))
            }
          )
          5 -> TransitionsPanel(
            selectedClip = selectedClip,
            onUpdateClip = { updated ->
              val list = currentProject.clips.map { if (it.id == updated.id) updated else it }
              pushState(currentProject.copy(clips = list))
            }
          )
          6 -> MediaLibraryPanel(
            onAddClip = { newClip ->
              val newStart = currentProject.clips
                .filter { it.trackIndex == newClip.trackIndex }
                .maxOfOrNull { it.startTimeMs + it.durationMs } ?: 0L
              val positioned = newClip.copy(startTimeMs = newStart)
              val updatedDuration = (newStart + newClip.durationMs).coerceAtLeast(currentProject.durationMs)
              pushState(
                currentProject.copy(
                  clips = currentProject.clips + positioned,
                  durationMs = updatedDuration
                )
              )
              selectedClipId = positioned.id
            }
          )
        }
      }
    }
  }

  if (showExportDialog) {
    ExportDialog(
      project = currentProject,
      isProUser = isProUser,
      onDismiss = { showExportDialog = false },
      onOpenUpgradeDialog = {
        showExportDialog = false
        showSubscriptionDialog = true
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
private fun EditorTopBar(
  project: VideoProject,
  canUndo: Boolean,
  canRedo: Boolean,
  cloudSavedNotice: Boolean,
  onBack: () -> Unit,
  onUndo: () -> Unit,
  onRedo: () -> Unit,
  onSave: () -> Unit,
  onExport: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(AkBgSurface)
      .padding(horizontal = 8.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AkTextPrimary)
      }

      Spacer(modifier = Modifier.width(4.dp))

      Column {
        Text(
          text = project.title,
          color = AkTextPrimary,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          maxLines = 1
        )
        Text(
          text = "${project.aspectRatio.label} • ${project.resolution.label.take(5)}",
          color = AkCyanAccent,
          fontSize = 10.sp
        )
      }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
      IconButton(onClick = onUndo, enabled = canUndo, modifier = Modifier.size(32.dp)) {
        Icon(
          Icons.AutoMirrored.Filled.Undo,
          contentDescription = "Undo",
          tint = if (canUndo) AkTextPrimary else AkTextMuted,
          modifier = Modifier.size(18.dp)
        )
      }

      IconButton(onClick = onRedo, enabled = canRedo, modifier = Modifier.size(32.dp)) {
        Icon(
          Icons.AutoMirrored.Filled.Redo,
          contentDescription = "Redo",
          tint = if (canRedo) AkTextPrimary else AkTextMuted,
          modifier = Modifier.size(18.dp)
        )
      }

      Spacer(modifier = Modifier.width(4.dp))

      IconButton(onClick = onSave, modifier = Modifier.size(32.dp)) {
        Icon(
          Icons.Default.CloudUpload,
          contentDescription = "Save to Cloud",
          tint = if (cloudSavedNotice) AkSuccess else AkTextSecondary,
          modifier = Modifier.size(18.dp)
        )
      }

      Spacer(modifier = Modifier.width(6.dp))

      Button(
        onClick = onExport,
        colors = ButtonDefaults.buttonColors(containerColor = AkCyanAccent),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
          .height(34.dp)
          .testTag("export_workspace_button")
      ) {
        Icon(Icons.Default.Download, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Export", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
      }
    }
  }
}

@Composable
private fun EditorToolTabs(
  activeTab: Int,
  onSelectTab: (Int) -> Unit
) {
  val tabs = listOf(
    "AI Tools" to Icons.Default.AutoAwesome,
    "Grading" to Icons.Default.ColorLens,
    "Chroma/Mask" to Icons.Default.Layers,
    "Audio" to Icons.Default.Audiotrack,
    "Titles" to Icons.Default.Title,
    "Transitions" to Icons.Default.Transform,
    "Media Stock" to Icons.Default.VideoLibrary
  )

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFF100E21))
      .padding(horizontal = 4.dp, vertical = 2.dp)
      .horizontalScroll(rememberScrollState()),
    horizontalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    tabs.forEachIndexed { index, (label, icon) ->
      val isSelected = activeTab == index
      Surface(
        onClick = { onSelectTab(index) },
        shape = RoundedCornerShape(6.dp),
        color = if (isSelected) AkPurplePrimary else Color.Transparent,
        modifier = Modifier.padding(2.dp)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) AkTextPrimary else AkTextMuted,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = label,
            color = if (isSelected) AkTextPrimary else AkTextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
          )
        }
      }
    }
  }
}
