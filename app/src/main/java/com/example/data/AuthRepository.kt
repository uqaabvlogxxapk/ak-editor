package com.example.data

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.model.UserProfile
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

class AuthRepository(
  private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
  private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
  val currentUser = _currentUser.asStateFlow()

  private val _isProUser = MutableStateFlow(false)
  val isProUser = _isProUser.asStateFlow()

  init {
    auth.addAuthStateListener { firebaseAuth ->
      _currentUser.value = firebaseAuth.currentUser
    }
  }

  suspend fun signInWithGoogle(context: Context): Result<FirebaseUser> {
    return try {
      val credentialManager = CredentialManager.create(context)
      val serverClientId = context.getString(R.string.default_web_client_id)

      // Create raw nonce and hashed nonce for Google Sign-In
      val rawNonce = UUID.randomUUID().toString()
      val bytes = rawNonce.toByteArray()
      val md = MessageDigest.getInstance("SHA-256")
      val digest = md.digest(bytes)
      val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

      val googleIdOption = GetSignInWithGoogleOption.Builder(serverClientId)
        .setNonce(hashedNonce)
        .build()

      val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

      val result = credentialManager.getCredential(
        request = request,
        context = context
      )

      val credential = result.credential
      val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
      val idToken = googleIdTokenCredential.idToken

      val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
      val authResult = auth.signInWithCredential(firebaseCredential).await()
      val user = authResult.user ?: error("Firebase sign-in resulted in null user")
      _currentUser.value = user
      Result.success(user)
    } catch (e: GetCredentialCancellationException) {
      Log.i("AuthRepository", "Google Sign-In was cancelled by user: ${e.message}")
      Result.failure(e)
    } catch (e: Exception) {
      Log.e("AuthRepository", "Google Sign-In failed: ${e.message}", e)
      Result.failure(e)
    }
  }

  fun signOut() {
    auth.signOut()
    _currentUser.value = null
    _isProUser.value = false
  }

  fun setProEntitlement(isPro: Boolean) {
    _isProUser.value = isPro
  }

  suspend fun syncUserProfile(context: Context, user: FirebaseUser) {
    try {
      val databaseId = context.getString(R.string.firestore_database_id)
      val db = FirebaseFirestore.getInstance(databaseId)
      val userDoc = db.collection("users").document(user.uid)
      val snapshot = userDoc.get().await()

      if (!snapshot.exists()) {
        val newProfile = UserProfile(
          id = user.uid,
          email = user.email ?: "",
          displayName = user.displayName ?: "Creator",
          isPro = _isProUser.value,
          createdAt = System.currentTimeMillis().toString()
        )
        userDoc.set(newProfile.toMap()).await()
      } else {
        val proVal = snapshot.getBoolean("isPro") ?: false
        if (proVal) {
          _isProUser.value = true
        }
      }
    } catch (e: Exception) {
      Log.w("AuthRepository", "Sync user profile warning: ${e.message}")
    }
  }
}
