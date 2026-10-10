package com.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ai.GeminiAiService
import com.example.data.AuthRepository
import com.example.data.ProjectRepository
import com.example.model.TemplateLibrary
import com.example.model.VideoProject
import com.example.ui.screens.EditorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.AKEditorTheme
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // App Check intent extra wiring before setContent
    configureAppCheck(this, intent)

    setContent {
      AKEditorTheme {
        AKEditorApp(context = this)
      }
    }
  }

  private fun configureAppCheck(context: Context, intent: Intent?) {
    try {
      val firebaseAppCheck = FirebaseAppCheck.getInstance()
      val providerFactory = DebugAppCheckProviderFactory.getInstance()
      firebaseAppCheck.installAppCheckProviderFactory(providerFactory)
    } catch (e: Exception) {
      Log.w("MainActivity", "AppCheck configuration note: ${e.message}")
    }
  }
}

@Composable
fun AKEditorApp(context: Context) {
  val scope = rememberCoroutineScope()

  val authRepository = remember { AuthRepository() }
  val projectRepository = remember { ProjectRepository(context) }
  val aiService = remember { GeminiAiService() }

  val currentUser by authRepository.currentUser.collectAsState()
  val isProUser by authRepository.isProUser.collectAsState()
  val localProjects by projectRepository.localProjects.collectAsState()

  val projectsList = remember { mutableStateListOf<VideoProject>() }
  var activeProject by remember { mutableStateOf<VideoProject?>(null) }

  // Initial Seed Projects on first launch
  LaunchedEffect(Unit) {
    if (projectsList.isEmpty()) {
      val starter1 = TemplateLibrary.templates[0].createProject()
      val starter2 = TemplateLibrary.templates[1].createProject()
      projectsList.add(starter1)
      projectsList.add(starter2)
      projectRepository.addLocalProject(starter1)
      projectRepository.addLocalProject(starter2)
    }
  }

  // Observe Cloud / Local Projects for User
  LaunchedEffect(currentUser) {
    val uid = currentUser?.uid ?: ""
    projectRepository.observeCloudProjects(uid).collectLatest { cloudProjects ->
      if (cloudProjects.isNotEmpty()) {
        projectsList.clear()
        projectsList.addAll(cloudProjects)
      }
    }
  }

  if (activeProject != null) {
    EditorScreen(
      initialProject = activeProject!!,
      isProUser = isProUser,
      aiService = aiService,
      onSaveProject = { updated ->
        activeProject = updated
        val uid = currentUser?.uid ?: ""
        scope.launch {
          projectRepository.saveProjectToCloud(uid, updated)
        }
      },
      onToggleProEntitlement = { isPro ->
        authRepository.setProEntitlement(isPro)
      },
      onBackToHome = {
        activeProject = null
      },
      modifier = Modifier.fillMaxSize()
    )
  } else {
    HomeScreen(
      projects = projectsList,
      currentUser = currentUser,
      isProUser = isProUser,
      authRepository = authRepository,
      onOpenProject = { proj ->
        activeProject = proj
      },
      onDeleteProject = { projId ->
        projectsList.removeAll { it.id == projId }
        val uid = currentUser?.uid ?: ""
        scope.launch {
          projectRepository.deleteProjectFromCloud(uid, projId)
        }
      },
      onToggleProEntitlement = { isPro ->
        authRepository.setProEntitlement(isPro)
      },
      modifier = Modifier.fillMaxSize()
    )
  }
}
