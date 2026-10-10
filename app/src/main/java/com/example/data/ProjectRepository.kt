package com.example.data

import android.content.Context
import android.util.Log
import com.example.R
import com.example.model.AspectRatio
import com.example.model.ExportFps
import com.example.model.ExportResolution
import com.example.model.VideoProject
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ProjectRepository(
  private val databaseId: String,
  private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(databaseId)
) {
  constructor(context: Context) : this(
    context.applicationContext.getString(R.string.firestore_database_id)
  )

  private val moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()
  private val projectAdapter = moshi.adapter(VideoProject::class.java)

  // Local in-memory cache for offline and instant editing
  private val _localProjects = MutableStateFlow<List<VideoProject>>(emptyList())
  val localProjects = _localProjects.asStateFlow()

  fun addLocalProject(project: VideoProject) {
    val current = _localProjects.value.toMutableList()
    val index = current.indexOfFirst { it.id == project.id }
    if (index >= 0) {
      current[index] = project
    } else {
      current.add(0, project)
    }
    _localProjects.value = current
  }

  fun deleteLocalProject(projectId: String) {
    _localProjects.value = _localProjects.value.filter { it.id != projectId }
  }

  suspend fun saveProjectToCloud(userId: String, project: VideoProject): Result<Unit> {
    if (userId.isEmpty()) {
      addLocalProject(project)
      return Result.success(Unit)
    }
    return try {
      val json = projectAdapter.toJson(project)
      val dataMap = mapOf(
        "id" to project.id,
        "userId" to userId,
        "title" to project.title,
        "aspectRatio" to project.aspectRatio.name,
        "resolution" to project.resolution.name,
        "fps" to project.fps.fps,
        "durationMs" to project.durationMs.toInt(),
        "clipCount" to project.clips.size,
        "trackCount" to 3,
        "projectDataJson" to json,
        "createdAt" to (project.createdAt.ifEmpty { System.currentTimeMillis().toString() }),
        "updatedAt" to System.currentTimeMillis().toString()
      )

      firestore.collection("users")
        .document(userId)
        .collection("projects")
        .document(project.id)
        .set(dataMap)
        .await()

      // Also ensure local cache is up to date
      addLocalProject(project)
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e("ProjectRepository", "Failed to save project to Firestore: ${e.message}", e)
      // Save locally anyway
      addLocalProject(project)
      Result.failure(e)
    }
  }

  fun observeCloudProjects(userId: String): Flow<List<VideoProject>> = callbackFlow {
    if (userId.isEmpty()) {
      trySend(_localProjects.value)
      awaitClose { }
      return@callbackFlow
    }

    val collectionRef = firestore.collection("users")
      .document(userId)
      .collection("projects")

    val listener: ListenerRegistration = collectionRef.addSnapshotListener { snapshot, error ->
      if (error != null) {
        Log.w("ProjectRepository", "Listen failed: ${error.message}")
        trySend(_localProjects.value)
        return@addSnapshotListener
      }

      if (snapshot != null) {
        val projects = snapshot.documents.mapNotNull { doc ->
          val json = doc.getString("projectDataJson")
          if (!json.isNullOrEmpty()) {
            try {
              projectAdapter.fromJson(json)
            } catch (e: Exception) {
              null
            }
          } else {
            // Fallback reconstruct
            VideoProject(
              id = doc.getString("id") ?: doc.id,
              userId = userId,
              title = doc.getString("title") ?: "Untitled",
              aspectRatio = AspectRatio.valueOf(doc.getString("aspectRatio") ?: AspectRatio.RATIO_16_9.name),
              resolution = ExportResolution.valueOf(doc.getString("resolution") ?: ExportResolution.RES_1080P.name),
              fps = ExportFps.entries.find { it.fps == (doc.getLong("fps")?.toInt() ?: 30) } ?: ExportFps.FPS_30,
              durationMs = doc.getLong("durationMs") ?: 10000L,
              createdAt = doc.getString("createdAt") ?: "",
              updatedAt = doc.getString("updatedAt") ?: ""
            )
          }
        }
        // Merge with local projects
        val merged = (projects + _localProjects.value).distinctBy { it.id }
        _localProjects.value = merged
        trySend(merged)
      }
    }

    awaitClose {
      listener.remove()
    }
  }

  suspend fun deleteProjectFromCloud(userId: String, projectId: String): Result<Unit> {
    deleteLocalProject(projectId)
    if (userId.isEmpty()) return Result.success(Unit)

    return try {
      firestore.collection("users")
        .document(userId)
        .collection("projects")
        .document(projectId)
        .delete()
        .await()
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e("ProjectRepository", "Failed to delete project from Firestore: ${e.message}", e)
      Result.failure(e)
    }
  }

  fun serializeProject(project: VideoProject): String {
    return projectAdapter.toJson(project)
  }

  fun deserializeProject(json: String): VideoProject? {
    return try {
      projectAdapter.fromJson(json)
    } catch (e: Exception) {
      null
    }
  }
}
